import React from "react";
import PhoneInput from "mui-phone-input-ssr";
import { showErrorOnChange } from "mui-rff";

const locale = {
  Ukraine: "Україна",
  Russia: "Россия",
  Belarus: "Беларусь",
};

export const PhoneNumber = (props) => {
  const {
    input: { value, onChange, ...restInput },
    meta,
    required,
    helperText,
    showError = showErrorOnChange,
    ...rest
  } = props;

  const { error, submitError } = meta;
  const isError = showError({ meta });

  function handleChange(val, country) {
    if (country.countryCode === "ua") {
      onChange(val.replace("+380 (0", "+380 ("));
      return;
    }
    onChange(val);
  }

  return (
    <PhoneInput
      color="secondary"
      required
      label="Номер телефона"
      error={isError}
      helperText={isError ? error || submitError : helperText}
      type="tel"
      fullWidth
      value={value}
      onChange={handleChange}
      defaultCountry="ua"
      regions="europe"
      localization={locale}
      onlyCountries={["ua", "ru", "by", "fr"]}
      variant="filled"
      inputProps={{ required, ...restInput }}
      {...rest}
    />
  );
};
