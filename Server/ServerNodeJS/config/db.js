const mongoose = require('mongoose');
const local = "mongodb+srv://huongvxph40510:huongvxph40510@cluster0.1dczug4.mongodb.net/AND103";

const connect = async () => {
    try {
        await mongoose.connect(local);
        console.log('Connect success');
    } catch (error) {
        console.error('Connection to MongoDB failed:', error);
    }
}

module.exports = { connect };
