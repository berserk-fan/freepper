import "../styles/globals.css";
import "../styles/tailwind.css";
import "../styles/fonts.css";
import React from "react";
import { AppProps } from "next/app";
import Head from "next/head";
import { Provider } from "react-redux";
import { StyledEngineProvider, CssBaseline } from "@mui/material";
import { ThemeProvider } from "@mui/material/styles";
import dynamic from "next/dynamic";
import { store } from "store";

import theme from "../theme";

dynamic(() => import("abortcontroller-polyfill/dist/polyfill-patch-fetch"));

export default function MyApp({ Component, pageProps }: AppProps) {
  return (
    <>
      <Head>
        <title>Погладить можно?</title>
      </Head>
      <StyledEngineProvider>
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
