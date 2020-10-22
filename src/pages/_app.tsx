import '../styles/globals.css'
import '../styles/tailwind.css'
import 'fontsource-roboto';
import React from "react";
import {AppProps} from "next/app";
import Head from "next/head";

function MyApp({Component, pageProps}: AppProps) {
    return (
        <>
            <Head>
                <title>Магазин</title>
            </Head>
            <Component {...pageProps} />
        </>
    )
}

export default MyApp
