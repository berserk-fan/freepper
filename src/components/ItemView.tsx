import React from 'react';
import {makeStyles} from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import CardMedia from '@material-ui/core/CardMedia';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import {Product} from "../model/Model";

const useStyles = makeStyles({
    root: {
        maxWidth: 345,
    },
    media: {
        height: 280,
    },
});

export default function ItemView(props: { item: Product, className?: string }) {
    const classes = useStyles();
    const {item, className} = props;
    const {displayName, description, id, image, color, size} = item;

    return (
        <Card className={`${classes.root} ${className || ''}`}>
            <CardActionArea>
                <CardMedia image={image} className={`${classes.media}`} title={displayName}/>
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
