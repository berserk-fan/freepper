import Box from "@material-ui/core/Box";
import theme from "../../theme";
import { IconButton, Snackbar } from "@material-ui/core";
import CloseIcon from "@material-ui/icons/Close";
import ContactUs from "./ContactUs";
import React, { useState } from "react";

export default function ContactUsSnackBar({
  open,
  close,
}: {
  open: boolean;
  close: () => void;
}) {
  function handleClose(ev) {
    ev.stopPropagation();
    close();
  }
  return (
    <Snackbar
      open={open}
      anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
    >
      <Box bgcolor={theme.palette.grey["50"]} position={"relative"}>
        <IconButton
          style={{
            position: "absolute",
            right: "-46px",
            backgroundColor: theme.palette.grey["50"],
            border: "1px solid #e0e0e0",
          }}
          aria-label="закрыть"
          color="inherit"
          onClick={handleClose}
        >
          <CloseIcon fontSize="small" />
        </IconButton>
        <ContactUs />
      </Box>
    </Snackbar>
  );
}
