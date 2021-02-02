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

export default function useErrorHandling(
  onComplete: () => void,
  maxClientRetries: number,
  maxServerRetries: number,
  retryPeriod: number
): [SubmitState, (t: any) => Promise<void>, number] {
  const [submitState, setSubmitState] = useState<SubmitState>("NOT_SUBMITTED");
  useEffect(() => {
    if (submitState === "OK") {
      onComplete();
    }
  }, [submitState]);
  const [retryNumber, setRetryNumber] = useState(0);

  function makeACall(axiosCall: () => Promise<Response>): Promise<void> {
    setSubmitState("SENDING");
    return promiseRetry(
      async (retry, retryNumber) => {
        setRetryNumber(retryNumber);
        if (retryNumber !== 1) {
          setSubmitState("RETRYING");
        }
        let newState: SubmitState = "OK";
        try {
          const res = await axiosCall();
          if (res.status != 201) {
            newState = "SERVER_ERROR";
          }
        } catch (err) {
          newState = "CLIENT_ERROR";
        }
        if (
          (newState === "CLIENT_ERROR" && retryNumber <= maxClientRetries) ||
          (newState === "SERVER_ERROR" && retryNumber <= maxServerRetries)
        ) {
          newState = "RETRY_TIMEOUT";
        }

        setSubmitState(newState);
        if (newState === "RETRY_TIMEOUT") {
          retry(new Error("Error posting order"));
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

  return [submitState, makeACall, retryNumber];
}
