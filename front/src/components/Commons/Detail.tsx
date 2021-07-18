import React from "react";
import HelpOutlineIcon from "@material-ui/icons/HelpOutline";
import CloseIcon from "@material-ui/icons/CancelOutlined";
import makeStyles from "@material-ui/core/styles/makeStyles";
import ClickAwayListener from "@material-ui/core/ClickAwayListener/ClickAwayListener";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";
import Button from "@material-ui/core/Button/Button";

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
