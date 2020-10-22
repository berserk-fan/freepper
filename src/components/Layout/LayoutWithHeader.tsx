import Header from "./Header";

export default function LayoutWithHeader({children}) {
    return (
        <>
            <Header/>
            {children}
        </>
    )
};
