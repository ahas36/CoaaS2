const electron = require('electron');

const app = electron.app;

const windowManager = require('electron-window-manager');

const protocol = electron.protocol;

const dialog = electron.dialog;
const BrowserWindow = electron.BrowserWindow;

const isDev = require('electron-is-dev');

const path = require('path');

let mainWindow=false;


function createWindow(args) {

    if (args == null) {
        process.argv.splice(1, 1);
    } else {
        process.argv[1] = args.path;
    }


    mainWindow = windowManager.createNew('home', 'CoaaS IDE', isDev ? 'http://localhost:3000?main' : `file://${path.join(__dirname, '../build/index.html?main')}`, false, {
        'width': 1024,
        'height': 768,
        'minWidth': 1024,
        'minHeight': 768,
        'position': 'center',
        'showDevTools': false,
        'resizable': true
    });
    mainWindow.open();

    mainWindow.object.on('close', function (event) {
        if (!true) {
            event.preventDefault() // Prevents the window from closing 
            dialog.showMessageBox({
                type: 'question',
                buttons: ['Yes', 'No'],
                title: 'Confirm',
                message: 'Are you sure you want to quit?'
            }, function (response) {
                if (response === 0) { // Runs the following if 'Yes' is clicked
                    app.showExitPrompt = false
                    quitApp(true);
                }
            })
        }
    });

}

const quitApp = (isHome)=>
{
    app.quit();
}

app.on('ready', () => {
    windowManager.init();
    createWindow();
});

app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {   
        app.quit();
    }
});

app.on('activate', () => {
    if (mainWindow.object === null) {
        createWindow();
    }
});
