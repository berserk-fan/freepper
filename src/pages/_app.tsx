import "../styles/globals.css";
import "../styles/tailwind.css";
import "fontsource-roboto";
import React from "react";
import { AppProps } from "next/app";
import Head from "next/head";
import { Provider } from "react-redux";
import { ThemeProvider } from "@material-ui/styles";
import theme from "../theme";
import { SnackbarProvider } from "notistack";
import { CssBaseline } from "@material-ui/core";
import setupStore from "../store/cofigStore"
import {PersistGate} from "redux-persist/integration/react";

function MyApp({ Component, pageProps }: AppProps) {
  const {store, persistor} = setupStore();
  return (
    <>
      <Head>
        <title>Погладить можно?</title>
      </Head>
      <SnackbarProvider maxSnack={3}>
        <ThemeProvider theme={theme}>
          <CssBaseline />
          <Provider store={store}>
              <PersistGate persistor={persistor}>
                  <Component {...pageProps} />
              </PersistGate>
          </Provider>
        </ThemeProvider>
      </SnackbarProvider>
    </>
  );
}

export default MyApp;
