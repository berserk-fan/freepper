import {Product} from "@mamat14/shop-server/shop_model";
import {
    Box,
    Button,
    Card,
    CardContent,
    IconButton,
    Popover,
    Typography
} from "@material-ui/core";
import Image from "next/image";
import PopupStateComponent, {bindPopover, bindToggle, bindTrigger} from "material-ui-popup-state";
import MoreVertIcon from "@material-ui/icons/MoreVert";
import DeleteIcon from "@material-ui/icons/Delete";
import CloseIcon from "@material-ui/icons/Close";
import React, {useState} from "react";
import {makeStyles} from "@material-ui/styles";
import AddCircleOutlineIcon from '@material-ui/icons/AddCircleOutline'
import RemoveCircleOutlineIcon from '@material-ui/icons/RemoveCircleOutline';
import theme from "../../theme";
import {CART, cartReducer} from "../../store";
import {PopupState} from "material-ui-popup-state/core";

function getAdditionalInfo({details}: Product) {
    switch (details.$case) {
        case "dogBed":
            const size = details.dogBed.sizes.find((s) => s.id == details.dogBed.sizeId);
            return (
                <div>
                    <Typography noWrap display={'inline'} variant={'caption'}>
                        Размер:
                    </Typography>
                    <Typography noWrap display={'inline'} variant={'h6'}>
                        {' ' + size.displayName}
                    </Typography>
                </div>
            )
    }
}

export function ActionsPopover(productId: string) {
    return (
        <PopupStateComponent variant="popover" popupId="cart-action-popover">
            {(popupState) => (
                <div>
                    <IconButton size={'small'} color={'primary'} {...bindTrigger(popupState)}><MoreVertIcon/></IconButton>
                    <Popover {...bindPopover(popupState)}
                             anchorOrigin={{
                                 vertical: 'top',
                                 horizontal: 'left',
                             }}
                             transformOrigin={{
                                 vertical: 'top',
                                 horizontal: 'right',
                             }}>
                        <div className={"flex flex-col"}>
                            <Button onClick={() => {
                                CART.delete(productId);
                                popupState.close()
                            }}
                                    fullWidth
                                    startIcon={<DeleteIcon/>}>
                                <Typography>
                                    Удалить из корзины
                                </Typography>
                            </Button>
                            <Button onClick={popupState.close} fullWidth startIcon={<CloseIcon/>}>
                                <Typography>Закрыть</Typography>
                            </Button>
                        </div>
                    </Popover>
                </div>)
            }
        </PopupStateComponent>);

}

const useStyles = makeStyles(({
    root: {
        height: 148,
        display: 'flex',
    },
    imageContainer: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minWidth: 106,
        maxWidth: 148,
        height: 148,
        flexShrink: 2,
        overflow: 'hidden'
    },
    image: {
        height: 148
    },
    dataContainer: {
        minWidth: 140,
        flexShrink: 1
    }
}));

export default function CartItem(props: { product: Product }) {
    const {displayName, price, image, id} = props.product;
    const classes = useStyles();
    const [count, setCount] = useState(CART.getProductCount(id));
    cartReducer.subscribe(() => {
        setCount(CART.getProductCount(id))
    });

    function handleChange(change: number) {
        return () => {
            CART.setProductCount(id, CART.getProductCount(id) + change)
        }
    }

    return (
        <Card variant={'outlined'} className={classes.root}>
            <div className={`${classes.imageContainer}`}>
                <div className={`${classes.image}`}>
                    <Image width={148} height={148} src={image.src} alt={image.alt}/>
                </div>
            </div>
            <div className={`flex flex-col justify-between flex-grow ${classes.dataContainer}`}>
                <CardContent>
                    <div className="flex flex-col justify-between">
                        <Typography noWrap align={'right'} variant={'h6'}>{displayName}</Typography>
                        <Typography noWrap color={'textSecondary'} align={'right'} variant={'h6'}>{price.price} ₴</Typography>
                    </div>
                </CardContent>
                <Box marginBottom={1} paddingLeft={1} paddingRight={2} className={"flex justify-between items-center"}>
                    <Box marginLeft={1}>
                        {getAdditionalInfo(props.product)}
                    </Box>
                    <Box marginLeft={1} className={"flex place-items-center"}>
                        <IconButton size={'small'} disabled={count <= 1} onClick={handleChange(-1)}>
                            <RemoveCircleOutlineIcon fontSize={'large'}/>
                        </IconButton>
                        <Typography variant={'h6'} classes={{root: 'select-none'}}>{CART.getProductCount(id)}</Typography>
                        <IconButton size={'small'} onClick={handleChange(1)}>
                            <AddCircleOutlineIcon fontSize={'large'}/>
                        </IconButton>
                        {ActionsPopover(id)}
                    </Box>
                </Box>
            </div>
        </Card>
    );
}
