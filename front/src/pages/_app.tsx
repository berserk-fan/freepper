import "../styles/globals.css";
import "../styles/tailwind.css";
import "../styles/fonts.css";
import React from "react";
import { AppProps } from "next/app";
import Head from "next/head";
import { Provider } from "react-redux";
import ThemeProvider from "@mui/styles/ThemeProvider";
import CssBaseline from "@mui/material/CssBaseline";
import dynamic from "next/dynamic";
import { store } from "store";
import StyledEngineProvider from "@mui/styled-engine/StyledEngineProvider";
import theme from "../theme";

dynamic(() => import("abortcontroller-polyfill/dist/polyfill-patch-fetch"));

export default function MyApp({ Component, pageProps }: AppProps) {
  return (
    <>
      <Head>
        <title>Погладить можно?</title>
      </Head>
      <StyledEngineProvider injectFirst>
        <ThemeProvider theme={theme}>
          <CssBaseline />
          <Provider store={store}>
            <Component {...pageProps} />
          </Provider>
        </ThemeProvider>
      </StyledEngineProvider>
    </>
  );
}
