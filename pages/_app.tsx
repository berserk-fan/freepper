import '../src/styles/globals.css'
import '../src/styles/tailwind.css'
import 'fontsource-roboto';

import React from "react";
import Header from "../src/components/Header";
import {AppProps} from "next/app";

function MyApp({Component, pageProps}: AppProps) {
    return (
        <>
            <Header/>
            <Component {...pageProps} />
        </>
    )
}

export default MyApp
