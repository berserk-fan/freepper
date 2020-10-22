import {Box} from "@material-ui/core";
import Image from "material-ui-image";
import Typography from "@material-ui/core/Typography";
import React from "react";
import {CardData} from "../../pages";

export default function HomeCard({cardData,center}: { cardData: CardData, center: boolean }) {
    const {src, alt, title, text} = cardData;
    return (
        <Box
            className={"max-w-sm rounded overflow-hidden shadow-lg" + (center ? " mx-auto" : "")}>
            <Image disableSpinner src={src} aspectRatio={2.0}/>
            <div className="px-6 py-4">
                <Typography gutterBottom={true} variant={"h6"}> {title} </Typography>
                <Typography color={"textSecondary"} variant={"body2"}> {text} </Typography>
            </div>
        </Box>)
}
