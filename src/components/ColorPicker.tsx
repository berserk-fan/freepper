import React from 'react';
import {makeStyles} from "@material-ui/core/styles";
import RadioGroup from "@material-ui/core/RadioGroup";
import FormControl from "@material-ui/core/FormControl";
import Radio, {RadioProps} from "@material-ui/core/Radio";
import CheckCircleIcon from '@material-ui/icons/CheckCircle';
import Box from "@material-ui/core/Box";
import {Color} from "../model/OldModel";

const useStyles = makeStyles({
    root: {
        color: (props: Color) => props.value,
        padding: 3,
    }
});

function ColoredRadio(props: { radioColor: Color } & RadioProps) {
    const classes = useStyles(props.radioColor);
    const {radioColor, ...other} = props;

    return <Radio color="default" {...other} className={`${classes.root}`}/>
}

type ColorPickerProps = {
    colors: Color[],
    itemId: string,
    onChange?: (colorId: Color) => void
}

export default function ColorPicker(props: ColorPickerProps) {
    const {colors, itemId, onChange} = props;
    const [selectedColorId, setColorId] = React.useState(colors[0].id);

    const handleColor = (event, newColorId) => {
        if(onChange) {
            onChange(colors.find((color) => color.id === newColorId));
        }
        setColorId(newColorId);
    };

    function filledBox(color) {
        return <Box display="flex" justifyContent="center" alignItems="center" width="24px" height="24px">
            <Box width="20px" height="20px" style={{
                borderRadius: "50%",
                backgroundColor: color.value
            }}/>
        </Box>;
    }

    return (
        <FormControl component="fieldset">
            <RadioGroup row aria-label="item color" name={`${itemId}-color`} value={selectedColorId} onChange={handleColor}>
                {colors.map(color =>
                    <ColoredRadio key={color.id}
                                  value={color.id}
                                  radioColor={color}
                                  size='small'
                                  aria-label={color.displayName}
                                  icon={filledBox(color)}
                                  checkedIcon={<CheckCircleIcon width={"20px"} height={"20px"}/>}/>
                )}
            </RadioGroup>
        </FormControl>
    );
}
