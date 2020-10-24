import React from 'react';
import {makeStyles} from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import CardMedia from '@material-ui/core/CardMedia';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import {Product} from "../model/OldModel";
import Link from "next/link";

const useStyles = makeStyles({
    root: {
        maxWidth: 345,
    },
    media: {
        height: 280,
    },
});

export default function ItemView(props: { product: Product, className?: string }) {
    const classes = useStyles();
    const {product, className} = props;
    const {displayName, description, id, image, color, size} = product;

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
                    <Link href={`/shop/${product.id}`}>
                        Купить
                    </Link>
                </Button>
                <Button size="small" color="primary">
                    <Link href={`/shop/${product.id}`}>
                        Подробнее
                    </Link>
                </Button>
            </CardActions>
        </Card>
    );
}
