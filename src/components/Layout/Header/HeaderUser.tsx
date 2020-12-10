import React, {memo, useState} from "react";
import {fullUserUpdateAction, StoreState, UserState} from "../../../store";
import {connect} from "react-redux";
import ExitToAppIcon from '@material-ui/icons/ExitToApp';
import AccountBoxIcon from '@material-ui/icons/AccountBox';
import {Box, Button, Fade, IconButton, LinearProgress, Omit, Popover, Typography} from "@material-ui/core";
import PopupStateComponent, {bindPopover, bindTrigger} from "material-ui-popup-state";
import CloseIcon from "@material-ui/icons/Close";
import MeetingRoomIcon from '@material-ui/icons/MeetingRoom';
import {makeValidateSync, showErrorOnBlur, TextField} from "mui-rff";
import {pathName1} from "../../../utils";
import {Form} from "react-final-form";
import {object, ObjectSchema, string} from "yup";

function UserPopover({logout}: { logout: () => Promise<void> }) {
    return (
        <PopupStateComponent variant="popover" popupId="user-header-popover">
            {(popupState) => (
                <div>
                    <IconButton{...bindTrigger(popupState)}>
                        <AccountBoxIcon fontSize={"large"}/>
                    </IconButton>
                    <Popover
                        {...bindPopover(popupState)}
                        anchorOrigin={{
                            vertical: "top",
                            horizontal: "left",
                        }}
                        transformOrigin={{
                            vertical: "top",
                            horizontal: "right",
                        }}
                    >
                        <div className={"flex flex-col"}>
                            <Button
                                onClick={() => {
                                    logout().then(() => console.log("Logging out"));
                                    popupState.close();
                                }}
                                fullWidth
                                startIcon={<MeetingRoomIcon/>}
                            >
                                <Typography>Выйти из аккаунта</Typography>
                            </Button>
                            <Button
                                onClick={popupState.close}
                                fullWidth
                                startIcon={<CloseIcon/>}
                            >
                                <Typography>Закрыть</Typography>
                            </Button>
                        </div>
                    </Popover>
                </div>
            )}
        </PopupStateComponent>
    )
}


type LoginForm = {
    email: string,
    password: string
}

const schema: ObjectSchema<LoginForm> = object({
    email: string().email("Неправильный формат электронной почты.").required("Введите электронную почту."),
    password: string().required("Введите пароль.")
});

const validate = makeValidateSync(schema);

function LoginPopover({login, updateUser}: { login: (email: string, password: string) => Promise<void>, updateUser: (_: UserState) => void }) {
    const [handling, setHandling] = useState(false);

    const handleSubmit = (close: () => void) => async (loginForm: LoginForm) => {
        setHandling(true);
        login(loginForm.email, loginForm.password).then(() => console.log(`Successfull login: ${loginForm.email} ${loginForm.password}`))
            .then(() => fetch(`/api/user/get?email=${loginForm.email}`))
            .then((_) => _.json())
            .then((user) => {
                setHandling(false);
                return user;
            })
            .then((user) => {
                close();
                return user;
            })
            .then(updateUser)
            .finally(() => {
                setHandling(false)
            })
    };

    return (
        <PopupStateComponent variant="popover" popupId="user-header-popover">
            {(popupState) => (
                <div>
                    <IconButton{...bindTrigger(popupState)}>
                        <ExitToAppIcon fontSize={"large"}/>
                    </IconButton>
                    <Popover
                        {...bindPopover(popupState)}
                        anchorOrigin={{
                            vertical: "top",
                            horizontal: "left",
                        }}
                        transformOrigin={{
                            vertical: "top",
                            horizontal: "right",
                        }}
                    >
                        <div className={"flex flex-col w-full"}>
                            <Form
                                {...{onSubmit: handleSubmit(popupState.close), validate}}
                                render={({handleSubmit, values}: { handleSubmit: any; values: LoginForm; }) => (
                                    <form noValidate>
                                        <div className={"flex flex-col mx-auto w-full gap-4 p-4"}>
                                            <TextField
                                                name={pathName1({} as LoginForm, "email")}
                                                required
                                                fullWidth
                                                id="email-input"
                                                label="Электронная почта"
                                                variant="filled"
                                                type="email"
                                                autoComplete={"email"}
                                                showError={showErrorOnBlur}
                                            />
                                            <TextField
                                                name={pathName1({} as LoginForm, "password")}
                                                required
                                                fullWidth
                                                id="password-input"
                                                label="Пароль"
                                                variant="filled"
                                                type="password"
                                                autoComplete={"password"}
                                                showError={showErrorOnBlur}
                                            />
                                            <div className={"flex m-2 justify-between"}>
                                                <Box>
                                                    <Box height={"4px"} marginTop={"-4px"} marginX={"2px"}>
                                                        <Fade
                                                            in={handling}
                                                            style={{
                                                                transitionDelay: handling ? '800ms' : '0ms',
                                                            }}
                                                            unmountOnExit
                                                        >
                                                            <LinearProgress
                                                                style={{
                                                                    borderRadius: '2px'
                                                                }}
                                                                color="secondary"/>
                                                        </Fade>
                                                    </Box>
                                                    <Button
                                                        type={"submit"}
                                                        onClick={(v) => {
                                                            handleSubmit(v);
                                                        }}
                                                        startIcon={<MeetingRoomIcon/>}
                                                        disabled={!schema.isValidSync(values)}
                                                    >
                                                        <Typography>Войти</Typography>
                                                    </Button>
                                                </Box>
                                                <Button
                                                    onClick={popupState.close}
                                                    startIcon={<CloseIcon/>}
                                                >
                                                    <Typography>Отмена</Typography>
                                                </Button>
                                            </div>
                                        </div>
                                    </form>
                                )}/>
                        </div>
                    </Popover>
                </div>
            )}
        </PopupStateComponent>
    );
}

function login(email: string, password: string): Promise<void> {
    console.log("logging in...");
    return new Promise<void>((resolve, reject) => setTimeout(() => resolve(), 2000))
}

function HeaderUser({userState, logout, updateUser}: { userState: UserState, logout: () => Promise<void>, updateUser: (_: UserState) => void }) {
    return (
        userState
            ? <UserPopover {...{logout}}/>
            : <LoginPopover {...{login, updateUser}}/>
    );
}

function mapStateToProps(state: StoreState) {
    return {
        userState: state.userState,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        logout: () => {
            dispatch(fullUserUpdateAction(null));
            return Promise.resolve();
        },
        updateUser: (_: UserState) => {
            dispatch(fullUserUpdateAction(_));
            return Promise.resolve()
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(memo(HeaderUser));
