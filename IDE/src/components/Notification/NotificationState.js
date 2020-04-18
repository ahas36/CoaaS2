export const initialState = {
    notification: null
};

export const SHOW_NOTIFICATION = "Layout/SHOW_NOTIFICATION";
let notificationID = 0;

export const showNotification = (notification) => ({
    type: SHOW_NOTIFICATION,
    notification
})

export default function NotificationReducer(state = initialState, action) {
    switch (action.type) {
        case SHOW_NOTIFICATION:
            action.notification.notificationID = notificationID++;
            return {
                ...state,
                notification: action.notification,
            };
        default:
            return state;
    }
}
