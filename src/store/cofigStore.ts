
import { createStore } from 'redux'
import { persistStore, persistReducer } from 'redux-persist'
import storage from 'redux-persist/lib/storage'
import {storeReducer} from "./index"; // defaults to localStorage for web

const persistConfig = {
    key: 'root',
    storage,
};

const persistedReducer = persistReducer(persistConfig, storeReducer);

export default () => {
    let store = createStore(persistedReducer);
    let persistor = persistStore(store as any);
    return { store, persistor }
}
