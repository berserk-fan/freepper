import React, {useEffect, useState} from "react";
import {Slide, Snackbar, Typography} from "@material-ui/core";
import {Alert} from "@material-ui/lab";
import {SubmitState} from "./UseErrorHandling";
import { Offline } from "react-detect-offline";

function CountDown({countDownId, periodSec}: {
    countDownId: number;
    periodSec: number;
}) {
    const [time, setTime] = useState(-1);
    useEffect(() => {
        setTime(periodSec);
        countDown(countDownId, periodSec);
    }, [countDownId]);

    function countDown(idAtStart: number, seconds: number) {
        setTimeout(() => {
            const newTime = seconds - 1;
            if (idAtStart === countDownId && newTime > 0) {
                setTime(newTime);
                countDown(idAtStart, newTime);
            }
        }, 1000);
    }

    return (
        <Typography component={"span"} display={"inline"}>
            {time.toString()}
        </Typography>
    );
}

export default function ErrorSnackbars({submitState, retryNumber, retryPeriodSec, content, errorMessage, successMessage, retryingMessage}: {submitState: SubmitState, retryNumber: number, retryPeriodSec: number, content: React.ReactNode, errorMessage: React.ReactNode, successMessage: React.ReactNode, retryingMessage: React.ReactNode}) {
    const isRetryState = submitState === "RETRY_TIMEOUT" || submitState === "RETRYING";
    const isRetryTimeout = submitState === "RETRY_TIMEOUT";
    const isOk = submitState === "OK";
    const isServerError = submitState === "SERVER_ERROR";

    return (
        <>
            <Snackbar open={isOk} TransitionComponent={Slide}>
                <Alert severity={"success"}>
                    {successMessage}
                </Alert>
            </Snackbar>
            <Snackbar open={isRetryState} TransitionComponent={Slide}>
                <Alert severity={"warning"}>
                    {errorMessage}
                    <Offline>
                        <Typography>Скорее всего у вас пропал интернет</Typography>
                    </Offline>
                    {isRetryTimeout ? (
                        <Typography>
                            Попробую еще раз через{" "}
                            <CountDown
                                countDownId={retryNumber}
                                periodSec={retryPeriodSec}
                            />
                        </Typography>
                    ) : (
                        <>{retryingMessage}</>
                    )}
                </Alert>
            </Snackbar>
            <Snackbar open={isServerError} TransitionComponent={Slide}>
                <Alert severity={"info"}>
                    {content}
                </Alert>
            </Snackbar>
        </>
    );
}
