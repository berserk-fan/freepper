import {Grid} from "@material-ui/core";
import React from "react";

export default function Spacing(props) {
    const {spacing, children, className = "", childClassName = "", ...otherProps} = props;
    return (
        <Grid container className={className} spacing={spacing} {...otherProps}>
            {children.map(c => <Grid item className={childClassName}>{c}</Grid>)}
        </Grid>
    )
}
