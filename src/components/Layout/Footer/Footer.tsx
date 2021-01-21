import React from "react";
import {Box, Divider, Link, Typography} from "@material-ui/core";
import ContactUs from "../../Checkout/ContactUs";
import { pages, supportPages } from "../Header/Header";
import theme from "../../../theme";

export default function Footer() {
  return (
    <Box component={"footer"} paddingTop={1} paddingBottom={10} bgcolor={theme.palette.grey["300"]}>
      <Box marginY={1}>
          <Divider variant={"middle"}/>
      </Box>
      <Box>
        <Typography style={{paddingLeft: theme.spacing(2)}} variant={"h4"}>Контакты</Typography>
        <ContactUs />
      </Box>
      <Box marginY={1}>
          <Divider variant={"middle"}/>
      </Box>
      <Box paddingX={2} className="flex flex-row" aria-label={"support-info"}>
        <Box width={"50%"}>
          {[
            pages.about,
            supportPages["delivery-and-payment-info"],
            supportPages["returns-policy"],
          ].map((page) => (
            <Box>
              <Link href={pages.about.path} color={"textPrimary"}>
                {page.name}
              </Link>
            </Box>
          ))}
        </Box>
        <Box width={"50%"}>
          {[supportPages.cooperation, supportPages["public-offer"]].map(
            (page) => (
              <Box>
                <Link href={page.path} color={"textPrimary"}>
                  {page.name}
                </Link>
              </Box>
            )
          )}
        </Box>
      </Box>
    </Box>
  );
}
