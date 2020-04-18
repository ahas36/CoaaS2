import { createStore, applyMiddleware } from 'redux';
import createSagaMiddelware from 'redux-saga';
import reducers from './reducers';
import root from './sagas';

const sagaMiddlware = createSagaMiddelware();

const store = createStore(
  reducers,
  applyMiddleware(sagaMiddlware)
);
sagaMiddlware.run(root)

export default store;
