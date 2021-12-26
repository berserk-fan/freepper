import React, { useCallback } from "react";
import PhoneInput from "mui-phone-input-ssr";
import { showErrorOnChange } from "mui-rff";

const locale = {
  Ukraine: "Україна",
  Russia: "Россия",
  Belarus: "Беларусь",
};

function handleChange(val, country, onChange) {
  if (country.countryCode === "ua") {
    onChange(val.replace("+380 (0", "+380 ("));
    return;
  }
  onChange(val);
}

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

  const handleChangeMemoized = useCallback(
    (val, country) => {
      handleChange(val, country, onChange);
    },
    [onChange],
  );

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
      onChange={handleChangeMemoized}
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
