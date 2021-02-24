import { Box, Container } from "@material-ui/core";
import React from "react";
import { MDXProvider } from "@mdx-js/react";
import LayoutWithHeaderAndFooter from "../Layout/LayoutWithHeaderAndFooter";
import { components } from "./components";

export default function MdPage(props) {
  return (
    <LayoutWithHeaderAndFooter>
      <Container maxWidth="md">
        <Box py={8}>
          <MDXProvider components={components}>
            <main {...props} />
          </MDXProvider>
        </Box>
      </Container>
    </LayoutWithHeaderAndFooter>
  );
}
