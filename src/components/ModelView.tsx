import React from 'react';
import {makeStyles} from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import CardMedia from '@material-ui/core/CardMedia';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import ColorPicker from "./ColorPicker";
import {Box} from "@material-ui/core";
import {ModelIndex} from "../../configs/Products";
import {Color} from "../model/Model";

const useStyles = makeStyles({
    root: {
        maxWidth: 345,
    },
    media: {
        height: 280,
    },
});

export default function ModelView(props: { modelIndex: ModelIndex, className?: string }) {
    const classes = useStyles();
    const {modelIndex, className} = props;
    const {displayName, description, id, image, colors, colorIdToImage} = modelIndex;
    const [currentImage, setCurrentImage] = React.useState(image);

    function setImage(color: Color) {
        const colorId = color.id;
        if (colorIdToImage) {
            const image = colorIdToImage.get(colorId) || currentImage;
            setCurrentImage(image);
        }
    }

    return (
        <Card className={`${classes.root} ${className || ''}`}>
            <CardActionArea>
                <CardMedia image={currentImage} className={`${classes.media}`} title={displayName}/>
                <Box className={`${(!colors || colors.length <= 1) ? "invisible" : ""}`}>
                    <ColorPicker itemId={id} colors={colors} onChange={setImage}/>
                </Box>
                <CardContent>
                    <Typography gutterBottom variant="h5" component="h2">
                        {displayName}
                    </Typography>
                    <Typography variant="body2" color="textSecondary" component="p">
                        {description}
                    </Typography>
                </CardContent>
            </CardActionArea>
            <CardActions>
                <Button size="small" color="primary">
                    Купить
                </Button>
                <Button size="small" color="primary">
                    Подробнее
                </Button>
            </CardActions>
        </Card>
    );
}
