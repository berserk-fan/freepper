import React from "react";
import { MDXProvider } from "@mdx-js/react";
import Container from "@material-ui/core/Container";
import Box from "@material-ui/core/Box";
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
