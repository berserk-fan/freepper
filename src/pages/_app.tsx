import "../styles/globals.css";
import "../styles/tailwind.css";
import "fontsource-roboto";
import React from "react";
import { AppProps } from "next/app";
import Head from "next/head";
import { store } from "../store";
import { Provider } from "react-redux";
import { ThemeProvider } from "@material-ui/styles";
import theme from "../theme";
import { SnackbarProvider } from "notistack";
import { CssBaseline } from "@material-ui/core";
import "typeface-roboto";

function MyApp({ Component, pageProps }: AppProps) {
  return (
    <>
      <Head>
        <title>Погладить можно?</title>
      </Head>
      <SnackbarProvider maxSnack={3}>
        <ThemeProvider theme={theme}>
          <CssBaseline />
          <Provider store={store}>
            <Component {...pageProps} />
          </Provider>
        </ThemeProvider>
      </SnackbarProvider>
    </>
  );
}

export default MyApp;
