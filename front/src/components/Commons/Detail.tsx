import React from "react";
import HelpOutlineIcon from "@mui/icons-material/HelpOutline";
import CloseIcon from "@mui/icons-material/CancelOutlined";
import makeStyles from "@mui/styles/makeStyles";
import ClickAwayListener from "@mui/base/ClickAwayListener";
import Tooltip, { TooltipProps } from "@mui/material/Tooltip";
import Button from "@mui/material/Button";

const useStyles = makeStyles({
  tooltip: {
    backgroundColor: "#FFFFFF",
    color: "black",
    maxWidth: 220,
    border: "1px solid #dadde9",
    padding: 10,
    margin: 0,
  },
});

export default function Detail(props: Omit<TooltipProps, "children">) {
  const classes = useStyles();
  const [open, setOpen] = React.useState(false);
  const handleTooltipClose = () => {
    setOpen(false);
  };

  const handleTooltipClicked = () => {
    setOpen((prev) => !prev);
  };

  return (
    <ClickAwayListener onClickAway={handleTooltipClose}>
      <Tooltip
        {...props}
        classes={classes}
        onClose={handleTooltipClose}
        open={open}
        disableFocusListener
        disableHoverListener
        disableTouchListener
        placement="bottom"
      >
        <Button color="secondary" size="small" onClick={handleTooltipClicked}>
          {open ? (
            <CloseIcon fontSize="small" />
          ) : (
            <HelpOutlineIcon fontSize="small" />
          )}
        </Button>
      </Tooltip>
    </ClickAwayListener>
  );
}
