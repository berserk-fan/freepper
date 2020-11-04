import {
  Box,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  TextField,
  Typography,
} from "@material-ui/core";
import React from "react";

export default function UserDetailsForm() {
  const [age, setAge] = React.useState("");

  const handleChange = (event: React.ChangeEvent<{ value: unknown }>) => {
    setAge(event.target.value as string);
  };
  return (
    <Box className={"flex flex-col gap-4"}>
      <Typography variant={"h6"}>Введите информацию о заказе</Typography>
      <Box>
        <TextField
          className={"w-full"}
          required
          id="outlined-required"
          label="Полное имя"
          variant="outlined"
        />
      </Box>
      <Box>
        <TextField
          className={"w-full"}
          required
          id="outlined-required"
          label="Адрес или отделение"
          variant="outlined"
        />
      </Box>
      <Box>
          <TextField
              className={"w-full"}
              required
              id="outlined-required"
              label="Номер телефона"
              variant="outlined"
              type='tel'
          />
      </Box>
      <Box>
        <FormControl variant="outlined" className={"w-full"}>
          <InputLabel id="demo-simple-select-outlined-label">
            Способ доставки
          </InputLabel>
          <Select
            labelId="demo-simple-select-outlined-label"
            id="demo-simple-select-outlined"
            value={age}
            onChange={handleChange}
            label="Способ доставки"
          >
            <MenuItem value={10}>Курьер</MenuItem>
            <MenuItem value={20}>В отделение</MenuItem>
          </Select>
        </FormControl>
      </Box>
    </Box>
  );
}
