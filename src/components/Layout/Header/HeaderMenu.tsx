import {Drawer, IconButton} from "@material-ui/core";
import MenuIcon from "@material-ui/icons/Menu";
import React from "react";
import {makeStyles} from "@material-ui/core/styles";
import Link from "next/link";
import Button from "@material-ui/core/Button";
import ButtonGroup from "@material-ui/core/ButtonGroup";


const useStyles = makeStyles(({
	list: {
		width: 250,
	},
	fullList: {
		width: 'auto',
	},
	menuButton: {
		height: 50
	},
	drawer: {
		width: '70vw'
	}
}));


export default function HeaderMenu() {
	const classes = useStyles();

	const [drawerOpen, setDrawerTo] = React.useState(false);

	const toggleDrawer = (open) => (event) => {
		if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
			return;
		}

		setDrawerTo(open);
	};

	return (
		<>
			<IconButton
				onClick={toggleDrawer(true)}
				edge="start"
				className={classes.menuButton}
				color="inherit"
				aria-label="menu"
			>
				<MenuIcon/>
			</IconButton>
			<Drawer classes={{paper: classes.drawer}} open={drawerOpen} onClose={toggleDrawer(false)}>
				<ButtonGroup orientation={'vertical'} className={``} color="primary" aria-label="page tabs">
					{
						[['/', 'Домой'], ['/shop', 'Магазин'], ['/about', 'О наc']].map(([path, name]) => {
							return (<Link href={path}>
								<Button className={classes.menuButton}>{name}</Button>
							</Link>)
						})
					}
				</ButtonGroup>
			</Drawer>
		</>
	)
}
