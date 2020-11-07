import React from "react";
import {
  makeStyles,
  Theme,
  createStyles,
  withStyles,
} from "@material-ui/core/styles";
import clsx from "clsx";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import StepConnector from "@material-ui/core/StepConnector";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import { StepIconProps } from "@material-ui/core/StepIcon";
import LocalShippingIcon from "@material-ui/icons/LocalShipping";
import PaymentIcon from "@material-ui/icons/Payment";
import CheckBoxIcon from "@material-ui/icons/CheckBox";
import Summary from "./Summary";
import UserDetailsForm from "./PostForm";
import { CartProduct } from "../../pages/checkout";
import { Container } from "@material-ui/core";
import PaymentForm from "./Payment";
import { Form } from "react-final-form";
import { string, object, ObjectSchema, mixed, number, array } from "yup";
import { makeValidate } from "mui-rff";
import {
  DeliveryDetails,
  DeliveryOption,
  DeliveryProvider,
  Order,
  PaymentOption,
} from "../../order-model";

const ColorlibConnector = withStyles({
  alternativeLabel: {
    top: 22,
  },
  active: {
    "& $line": {
      backgroundImage:
        "linear-gradient( 95deg,rgb(242,113,33) 0%,rgb(233,64,87) 50%,rgb(138,35,135) 100%)",
    },
  },
  completed: {
    "& $line": {
      backgroundImage:
        "linear-gradient( 95deg,rgb(242,113,33) 0%,rgb(233,64,87) 50%,rgb(138,35,135) 100%)",
    },
  },
  line: {
    height: 3,
    border: 0,
    backgroundColor: "#eaeaf0",
    borderRadius: 1,
  },
})(StepConnector);

const useColorlibStepIconStyles = makeStyles({
  root: {
    backgroundColor: "#ccc",
    zIndex: 1,
    color: "#fff",
    width: 50,
    height: 50,
    display: "flex",
    borderRadius: "50%",
    justifyContent: "center",
    alignItems: "center",
  },
  active: {
    backgroundImage:
      "linear-gradient( 136deg, rgb(242,113,33) 0%, rgb(233,64,87) 50%, rgb(138,35,135) 100%)",
    boxShadow: "0 4px 10px 0 rgba(0,0,0,.25)",
  },
  completed: {
    backgroundImage:
      "linear-gradient( 136deg, rgb(242,113,33) 0%, rgb(233,64,87) 50%, rgb(138,35,135) 100%)",
  },
});

function ColorlibStepIcon(props: StepIconProps) {
  const classes = useColorlibStepIconStyles();
  const { active, completed } = props;

  const icons: { [index: string]: React.ReactElement } = {
    1: <LocalShippingIcon />,
    2: <CheckBoxIcon />,
    3: <PaymentIcon />,
  };

  return (
    <div
      className={clsx(classes.root, {
        [classes.active]: active,
        [classes.completed]: completed,
      })}
    >
      {icons[String(props.icon)]}
    </div>
  );
}

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: "100%",
    },
    button: {
      marginRight: theme.spacing(1),
    },
    instructions: {
      marginTop: theme.spacing(1),
      marginBottom: theme.spacing(1),
    },
  })
);

function getButtonTexts() {
  return [
    "Подтвердить и перейти к проверке заказа",
    "Подтвердить и перейти к оплате заказа",
    "Оплатить заказ",
  ];
}

export type OrderForm = Omit<Partial<Order>, "deliveryDetails"> & {
  deliveryDetails: Partial<DeliveryDetails>;
};

function getStepContent(
  step: number,
  cartProducts: CartProduct[],
  orderData: OrderForm
) {
  switch (step) {
    case 0:
      return <UserDetailsForm order={orderData} />;
    case 1:
      return <Summary cartProducts={cartProducts} orderForm={orderData}/>;
    case 2:
      return <PaymentForm />;
    default:
      return "Unknown step";
  }
}

const deliveryDetailsSchema: ObjectSchema<DeliveryDetails> = object({
  address: string()
    .required("Пожалуйста, введите адрес")
    .max(500, "Слишком длинный адрес"),
  provider: mixed()
    .oneOf([DeliveryProvider.NOVAYA_POCHTA])
    .required()
    .default(DeliveryProvider.NOVAYA_POCHTA),
  fullName: string()
    .required("Пожалуйста, введите имя")
    .min(5, "Cлишком короткое имя")
    .max(500, "Слишком длинное имя"),
  phone: string()
    .required("Пожалуйста, введите номер телефона")
    .min(6, "Слишком короткий номер телефона")
    .max(500, "Слишком длинный номер телефона"),
  email: string()
    .required("Вам нужно ввести имэйл")
    .email("Неправильный электронный адресс")
    .max(500, "Слишком длинный имэйл"),
  option: mixed().oneOf([DeliveryOption.COURIER, DeliveryOption.TO_WAREHOUSE]),
});

const schema: ObjectSchema<OrderForm> = object({
  deliveryDetails: deliveryDetailsSchema,
  paymentOption: mixed().oneOf([PaymentOption.COD]).default(PaymentOption.COD),
});

const validate = makeValidate(schema);

function CheckoutForm({
  cartProducts,
  activeStep,
  handleBack,
  handleNext,
}: {
  cartProducts: CartProduct[];
  activeStep: number;
  handleBack: () => void;
  handleNext: () => void;
}) {
  const buttonTexts = getButtonTexts();
  function onSubmit() {}

  return (
    <Form
      onSubmit={onSubmit}
      validate={validate}
      render={({
        handleSubmit,
        values,
      }: {
        handleSubmit: any;
        values: OrderForm;
      }) => (
        <form onSubmit={handleSubmit} noValidate>
          <div>
            {getStepContent(activeStep, cartProducts, values)}
            <div>
              <Button disabled={activeStep === 0} onClick={handleBack}>
                Назад
              </Button>
              <Button variant="contained" color="primary" onClick={handleNext}>
                {buttonTexts[activeStep]}
              </Button>
            </div>
          </div>
        </form>
      )}
    />
  );
}

export default function Checkout({
  cartProducts,
}: {
  cartProducts: CartProduct[];
}) {
  const classes = useStyles();
  const steps = ["Доставка", "Проверка", "Оплата"];
  const [activeStep, setActiveStep] = React.useState(0);
  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };
  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };
  const handleReset = () => {
    setActiveStep(0);
  };

  return (
    <Container maxWidth={"md"} className={classes.root}>
      <Stepper
        alternativeLabel
        activeStep={activeStep}
        connector={<ColorlibConnector />}
      >
        {steps.map((label) => (
          <Step key={label}>
            <StepLabel StepIconComponent={ColorlibStepIcon}>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>
      <div>
        {activeStep === steps.length ? (
          <div>
            <Typography className={classes.instructions}>
              All steps completed - you&apos;re finished
            </Typography>
            <Button onClick={handleReset} className={classes.button}>
              Reset
            </Button>
          </div>
        ) : (
          <CheckoutForm
            cartProducts={cartProducts}
            activeStep={activeStep}
            handleBack={handleBack}
            handleNext={handleNext}
          />
        )}
      </div>
    </Container>
  );
}
