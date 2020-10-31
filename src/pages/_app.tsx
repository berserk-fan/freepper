import '../styles/globals.css'
import '../styles/tailwind.css'
import 'fontsource-roboto';
import React from "react";
import {AppProps} from "next/app";
import Head from "next/head";
import {cartReducer} from "../store";
import { Provider } from 'react-redux'

function MyApp({Component, pageProps}: AppProps) {
    return (
        <>
            <Head>
                <title>Магазин</title>
            </Head>
            <Provider store={cartReducer}>
                <Component {...pageProps} />
            </Provider>
        </>
    )
}

export default MyApp
