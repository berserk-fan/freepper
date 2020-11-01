import Header from "./Header/Header";
import ValueProp from "./Header/ValueProp";

export default function LayoutWithHeader({children, value=false}) {
    return (
        <>
            <Header/>
            {value ? <ValueProp/> : false}
            {children}
        </>
    )
};
