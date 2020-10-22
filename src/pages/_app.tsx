import '../styles/globals.css'
import '../styles/tailwind.css'
import 'fontsource-roboto';
import 'swiper/swiper-bundle.min.css';

import React from "react";
import Header from "../components/Header";
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
