import React from "react";
import {Button, Container, Typography} from "@material-ui/core";
import {pathName1} from "../utils";
import {OrderForm} from "../components/Checkout/CheckoutForm";
import {makeValidateSync, showErrorOnBlur, TextField} from "mui-rff";
import {Form} from "react-final-form";
import {mixed, object, ObjectSchema, string} from "yup";
import {DeliveryOption, DeliveryProvider, PaymentOption} from "../order-model";
import Header from "../components/Layout/Header/Header";
import {NextRouter, useRouter} from "next/router";

type RegisterForm = {
    email: string,
    name: string,
    password: string
}

export const registerSchema: ObjectSchema<RegisterForm> = object().shape({
    name: string()
        .required("Введите имя, пожалуйста")
        .min(5, "Cлишком короткое имя")
        .max(500, "Слишком длинное имя")
        .required(),
    email: string().email("Неправильный формат имейла.").required(),
    password: string().min(6).required("Введите пожалуйста пароль").required()
});

const validate = makeValidateSync(registerSchema);

const handleSubmit = (router: NextRouter) => async (registerForm: RegisterForm) => {
    const user = {
        fullName: registerForm.name,
        email: registerForm.email,
        password: registerForm.password
    };
    const response = await fetch("/api/user/register", {body: JSON.stringify(user), method: "POST"});
};

export default function Register() {
    const router = useRouter();

    return (<>
        <Header/>
        <Container maxWidth={"sm"}>
            <Form
                {...{onSubmit: handleSubmit(router), validate}}
                render={({handleSubmit, values}: { handleSubmit: any; values: RegisterForm; }) => (
                    <form noValidate>
                        <div className={"flex flex-col mx-auto w-full gap-4"}>
                            <Typography variant={"h3"} align={"center"}>
                                Логин
                            </Typography>
                            <TextField
                                name={pathName1({} as RegisterForm, "name")}
                                required
                                fullWidth
                                id="full-name-input"
                                label="Полное имя"
                                variant="filled"
                                type="text"
                                autoComplete={"name"}
                                showError={showErrorOnBlur}
                            />
                            <TextField
                                name={pathName1({} as RegisterForm, "email")}
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
                                name={pathName1({} as RegisterForm, "password")}
                                required
                                fullWidth
                                id="password-input"
                                label="Пароль"
                                variant="filled"
                                type="password"
                                autoComplete={"password"}
                                showError={showErrorOnBlur}
                            />
                            <div className={"mx-auto"}>
                                <Button color={"primary"}
                                        variant={"contained"}
                                        onClick={handleSubmit}
                                        type="submit"
                                        disabled={!registerSchema.isValidSync(values)}
                                >
                                    Зарегистрироваться
                                </Button>
                            </div>
                        </div>
                    </form>
                )}/>
        </Container>
    </>)
}
