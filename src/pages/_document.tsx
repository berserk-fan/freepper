import React from 'react';
import Document, {Html, Head, Main, NextScript} from 'next/document';
import {ServerStyleSheet} from 'styled-components'
import { ServerStyleSheets as MaterialStyleSheets } from '@material-ui/core/styles';
import theme from "../theme";

export default class MyDocument extends Document {
    render() {
        return (
            <Html lang="en">
                <Head>
                    {/* PWA primary color */}
                    <meta name="theme-color" content={theme.palette.primary.main}/>
                </Head>
                <body>
                <Main/>
                <NextScript/>
                </body>
            </Html>
        );
    }
}

MyDocument.getInitialProps = async (ctx) => {
    const sheet = new ServerStyleSheet()
    const materialSheet = new MaterialStyleSheets()
    const originalRenderPage = ctx.renderPage

    try {
        ctx.renderPage = () =>
            originalRenderPage({
                enhanceApp: (App) => (props) =>
                    sheet.collectStyles(
                        materialSheet.collect(<App {...props} />)
                    ),
            })

        const initialProps = await Document.getInitialProps(ctx)
        return {
            ...initialProps,
            styles: (
                <>
                    {initialProps.styles}
                    {materialSheet.getStyleElement()}
                    {sheet.getStyleElement()}
                </>
            ),
        }
    } finally {
        sheet.seal()
    }
};
