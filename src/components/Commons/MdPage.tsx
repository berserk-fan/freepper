import {Box, Container} from "@material-ui/core";
import Markdown from "./Renderers";
import LayoutWithHeaderAndFooter from "../Layout/LayoutWithHeaderAndFooter";
import React from "react";

export default function MdPage({pageMd}:{pageMd: string}) {
    return (
        <LayoutWithHeaderAndFooter>
            <Container maxWidth={"md"}>
                <Box py={8}>
                    <Markdown>
                        {pageMd}
                    </Markdown>
                </Box>
            </Container>
        </LayoutWithHeaderAndFooter>
    )
}
