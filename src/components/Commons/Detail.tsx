import { Button, ClickAwayListener, Tooltip } from "@material-ui/core";
import React from "react";
import HelpOutlineIcon from "@material-ui/icons/HelpOutline";
import CloseIcon from "@material-ui/icons/CancelOutlined";
import { makeStyles } from "@material-ui/styles";

const useStyles = makeStyles({
  tooltip: {
    backgroundColor: "#FFFFFF",
    color: "black",
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
  const handleTooltipClose = (ev: React.MouseEvent<Document>) => {
    setOpen(false);
  };

  const handleTooltipClicked = (ev) => {
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
