import React from "react";
import PhoneInput from "mui-phone-input-ssr";
import { showErrorOnChange, TextField } from "mui-rff";

const locale = {
  Ukraine: "Україна",
  Russia: "Россия",
  Belarus: "Беларусь",
};

export const PhoneNumber = (props) => {
  const {
    input: { name, value, type, onChange, ...restInput },
    meta,
    required,
    helperText,
    showError = showErrorOnChange,
    ...rest
  } = props;

  const { error, submitError } = meta;
  const isError = showError({ meta });

  function handleChange(value, country) {
    if (country.countryCode === "ua") {
      onChange(value.replace("+380 (0", "+380 ("));
      return;
    }
    onChange(value);
  }

  return (
    <PhoneInput
      color={"secondary"}
      required
      label={"Номер телефона"}
      error={isError}
      helperText={isError ? error || submitError : helperText}
      type={"tel"}
      fullWidth
      value={value}
      onChange={handleChange}
      defaultCountry={"ua"}
      regions={"europe"}
      localization={locale}
      onlyCountries={["ua", "ru", "by", "fr"]}
      variant={"filled"}
      inputProps={{ required, ...restInput }}
      {...rest}
    />
  );
};
