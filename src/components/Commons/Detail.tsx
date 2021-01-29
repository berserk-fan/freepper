import {ClickAwayListener, IconButton, Tooltip, Typography, withStyles} from "@material-ui/core";
import React from "react";
import HelpOutlineIcon from '@material-ui/icons/HelpOutline';
import theme from "../../theme";

const HtmlTooltip = withStyles(({
    tooltip: {
        backgroundColor: '#f5f5f9',
        color: 'rgba(0, 0, 0, 0.87)',
        maxWidth: 220,
        fontSize: theme.typography.pxToRem(12),
        border: '1px solid #dadde9',
        margin: 0,
        padding: 4
    },
}))(Tooltip);

const DetailButton = withStyles({
    root: {
        padding: 4
    }
})(IconButton);
export default function Detail(props: {text: string} & any) {
    const {text, ...rest} = props;
    const [open, setOpen] = React.useState(false);
    const handleTooltipClose = () => {
        setOpen(false);
    };

    const handleTooltipOpen = () => {
        setOpen(true);
    };

    return (<ClickAwayListener onClickAway={handleTooltipClose}>
            <HtmlTooltip
                onClose={handleTooltipClose}
                open={open}
                disableFocusListener
                disableHoverListener
                disableTouchListener
                title={<Typography variant={"caption"}>{text}</Typography>}
                placement={"bottom"}
                {...rest}
            >
                <DetailButton onClick={handleTooltipOpen}><HelpOutlineIcon fontSize={"small"}/></DetailButton>
            </HtmlTooltip>
    </ClickAwayListener>)
}
