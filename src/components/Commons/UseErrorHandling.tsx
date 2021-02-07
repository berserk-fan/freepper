import React, { useEffect, useState } from "react";
import promiseRetry from "promise-retry";

export type SubmitState =
  | "NOT_SUBMITTED"
  | "SENDING"
  | "OK"
  | "RETRY_TIMEOUT"
  | "RETRYING"
  | "CLIENT_ERROR"
  | "SERVER_ERROR"
  | "CANCELLED";

type UseErrorHandlingResult = {
  submitState: SubmitState;
  customFetch: (r: RequestInfo, i: RequestInit) => Promise<void>;
  currentRetry: number;
  cancel: () => void;
  reset: () => void;
};

export default function useErrorHandling(
  onComplete: () => void,
  maxServerRetries: number,
  retryPeriod: number
): UseErrorHandlingResult {
  console.log("USE ERROR HANDLING");
  const [submitState, setSubmitState] = useState<SubmitState>("NOT_SUBMITTED");
  const [retryNumber, setRetryNumber] = useState(0);

  function changeState(state: SubmitState) {
    setSubmitState(state);
    if (submitState === "OK") {
      onComplete();
    }
  }

  let controller =
    typeof window === "undefined" ? undefined : new AbortController();
  let signal = typeof window === "undefined" ? undefined : controller.signal;
  function cancel() {
    console.log("CANCELLING");
    controller.abort();
    controller = new AbortController();
    signal = controller.signal;
  }

  function reset() {
    console.log("RESETTING");
    setSubmitState("NOT_SUBMITTED");
    cancel();
  }

  function customFetch(r: RequestInfo, i: RequestInit): Promise<void> {
    changeState("SENDING");
    return promiseRetry(
      async (retry, retryNumber) => {
        console.log("RETRYING" + submitState);
        if (
          submitState === "CANCELLED" ||
          (retryNumber != 1 && submitState === "NOT_SUBMITTED")
        ) {
          return;
        }
        console.log("RETRYING 2");
        setRetryNumber(retryNumber);
        if (retryNumber !== 1) {
          changeState("RETRYING");
        }
        let newState: SubmitState = "OK";
        try {
          const res = await fetch(r, { ...i, signal: signal });
          if (res.status != 201) {
            newState = "SERVER_ERROR";
          }
        } catch (err) {
          console.log("CAUGHT ERROR");
          if (err.name === "AbortError") {
            newState = "CANCELLED";
            if (submitState === "NOT_SUBMITTED") {
              //for reset
              return;
            }
          } else {
            newState = "CLIENT_ERROR";
          }
        }

        if (newState === "SERVER_ERROR" && retryNumber <= maxServerRetries) {
          newState = "RETRY_TIMEOUT";
        }

        console.log("CHANGING STATE " + newState);
        changeState(newState);
        if (newState === "RETRY_TIMEOUT") {
          retry(new Error("Error during fetch"));
        }
      },
      {
        retries: 100,
        factor: 1,
        minTimeout: retryPeriod * 1000,
        maxTimeout: retryPeriod * 1000,
      }
    );
  }

  return {
    submitState,
    customFetch: customFetch,
    currentRetry: retryNumber,
    cancel,
    reset,
  };
}
