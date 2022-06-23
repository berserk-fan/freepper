import React from "react";
import HelpOutlineIcon from "@mui/icons-material/HelpOutline";
import CloseIcon from "@mui/icons-material/CancelOutlined";
import makeStyles from "@mui/material/styles/makeStyles";
import ClickAwayListener from "@mui/base/ClickAwayListener";
import Tooltip from "@mui/material/Tooltip/Tooltip";
import Button from "@mui/material/Button/Button";

const useStyles = makeStyles({
  tooltip: {
    backgroundColor: "#FFFFFF",
    color: "kr_black",
    maxWidth: 220,
    border: "1px solid #dadde9",
    padding: 4,
    margin: 0,
  },
});

export default function Detail(props: { text: string } & any) {
  const classes = useStyles();
  const { text, ...rest } = props;
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
        classes={classes}
        onClose={handleTooltipClose}
        open={open}
        disableFocusListener
        disableHoverListener
        disableTouchListener
        title={text}
        placement="bottom"
        {...rest}
      >
        <Button size="small" onClick={handleTooltipClicked}>
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
