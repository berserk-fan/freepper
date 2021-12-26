import Box from "@material-ui/core/Box";
import IconButton from "@material-ui/core/IconButton/IconButton";
import Snackbar from "@material-ui/core/Snackbar/Snackbar";
import useTheme from "@material-ui/core/styles/useTheme";
import CloseIcon from "@material-ui/icons/Close";
import React, { useCallback } from "react";
import ContactUs from "./ContactUs";

function handleClose(ev, close) {
  ev.stopPropagation();
  close();
}

export default function ContactUsSnackBar({
  open,
  close,
}: {
  open: boolean;
  close: () => void;
}) {
  const theme = useTheme();
  const handleCloseMemoized = useCallback(
    (ev) => {
      handleClose(ev, close);
    },
    [close],
  );
  return (
    <Snackbar
      open={open}
      anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
    >
      <Box bgcolor={theme.palette.grey["50"]} position="relative">
        <IconButton
          style={{
            position: "absolute",
            right: "-46px",
            backgroundColor: theme.palette.grey["50"],
            border: "1px solid #e0e0e0",
          }}
          aria-label="закрыть"
          color="inherit"
          onClick={handleCloseMemoized}
        >
          <CloseIcon fontSize="small" />
        </IconButton>
        <ContactUs />
      </Box>
    </Snackbar>
  );
}
