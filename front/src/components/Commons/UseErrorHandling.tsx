import { useEffect, useRef, useState } from "react";
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
  retryPeriod: number,
): UseErrorHandlingResult {
  const [submitState, setSubmitState] = useState<SubmitState>("NOT_SUBMITTED");
  const [retryNumber, setRetryNumber] = useState(0);
  const currentState = useRef("NOT_SUBMITTED");
  const abortController = useRef<AbortController>(null);

  useEffect(() => {
    abortController.current = new AbortController();
  }, []);

  function changeState(state: SubmitState) {
    currentState.current = state;
    setSubmitState(state);
    if (submitState === "OK") {
      try {
        onComplete();
      } catch (e) {
        console.error(e);
      }
    }
  }

  function cancel() {
    abortController.current.abort();
    abortController.current = new AbortController();
  }

  function reset() {
    changeState("NOT_SUBMITTED");
    cancel();
  }

  function customFetch(r: RequestInfo, i: RequestInit): Promise<void> {
    changeState("SENDING");
    return promiseRetry(
      async (retry, curRetryNumber) => {
        const curState = currentState.current;
        if (
          curState === "CANCELLED" ||
          (curRetryNumber !== 1 && curState === "NOT_SUBMITTED")
        ) {
          return;
        }
        setRetryNumber(curRetryNumber);
        if (curRetryNumber !== 1) {
          changeState("RETRYING");
        }
        let newState: SubmitState = "OK";
        try {
          const res = await fetch(r, {
            ...i,
            signal: abortController.current.signal,
          });
          if (res.status !== 201) {
            newState = "SERVER_ERROR";
          }
        } catch (err) {
          if (err.name === "AbortError") {
            newState = "CANCELLED";
            if (curState === "NOT_SUBMITTED") {
              // for reset
              return;
            }
          } else {
            newState = "CLIENT_ERROR";
          }
        }

        if (newState === "SERVER_ERROR" && curRetryNumber <= maxServerRetries) {
          newState = "RETRY_TIMEOUT";
        }

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
      },
    );
  }

  return {
    submitState,
    customFetch,
    currentRetry: retryNumber,
    cancel,
    reset,
  };
}
