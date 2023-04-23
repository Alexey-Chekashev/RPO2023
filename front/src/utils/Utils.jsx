class Utils {
    saveUser(user) {
        window.sessionStorage.setItem('user', JSON.stringify(user));
    }

    removeUser() {
        window.sessionStorage.removeItem('user');
    }

    getToken() {
        let user = JSON.parse(window.sessionStorage.getItem('user'));
        return user&&"Bearer " + user.token;
    }

    getUserName() {
        let user = JSON.parse(window.sessionStorage.getItem('user'));
        return user && user.login;
    }

    getUser() {
        return JSON.parse(window.sessionStorage.getItem('user'));
    }
}

// eslint-disable-next-line import/no-anonymous-default-export
export default new Utils();