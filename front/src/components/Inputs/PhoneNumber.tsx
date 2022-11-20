import React, { ComponentType, useCallback } from "react";
import PhoneInput, { MuiPhoneNumberProps } from "material-ui-phone-number";
import { showErrorOnChange } from "mui-rff";
import { FieldRenderProps } from "react-final-form";

function handleChange(val: string, onChange) {
  if (val.startsWith("+380")) {
    onChange(val.replace("+380 (0", "+380 ("));
    return;
  }
  onChange(val);
}

const PhoneNumber: ComponentType<FieldRenderProps<MuiPhoneNumberProps>> = (
  props,
) => {
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
    (val) => {
      handleChange(val, onChange);
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
      onlyCountries={["ua", "fr"]}
      variant="filled"
      inputProps={{ required, ...restInput }}
      {...rest}
    />
  );
};

export default PhoneNumber;
