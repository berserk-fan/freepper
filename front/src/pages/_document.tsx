import React from "react";
import Document, { Html, Head, Main, NextScript } from "next/document";
import ServerStyleSheets from "@mui/styles/ServerStyleSheets";
import theme from "../theme";

export default class MyDocument extends Document {
  render() {
    return (
      <Html lang="ru">
        <Head>
          <meta name="theme-color" content={theme.palette.primary.main} />
          <link
            rel="icon"
            type="image/png"
            sizes="32x32"
            href="https://pomo.imgix.net/favicon-32x32.png"
          />
          <link
            rel="icon"
            type="image/png"
            sizes="16x16"
            href="https://pomo.imgix.net/favicon-16x16.png"
          />
          <meta
            name="apple-mobile-web-app-status-bar-style"
            content="black-translucent"
          />
        </Head>
        <body>
          <Main />
          <NextScript />
        </body>
      </Html>
    );
  }
}

MyDocument.getInitialProps = async (ctx) => {
  const sheets = new ServerStyleSheets();
  const originalRenderPage = ctx.renderPage;

  ctx.renderPage = () =>
    originalRenderPage({
      enhanceApp: (App) => (props) => sheets.collect(<App {...props} />),
    });

  const initialProps = await Document.getInitialProps(ctx);

  return {
    ...initialProps,
    // Styles fragment is rendered after the app and page rendering finish.
    styles: [
      ...React.Children.toArray(initialProps.styles),
      sheets.getStyleElement(),
    ],
  };
};
