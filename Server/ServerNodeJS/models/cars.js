const mongoose = require('mongoose');
const Scheme = mongoose.Schema;

const Cars = new Scheme({
    ten: {
        type: String,
    },
    namSX: {
        type: Number
    },
    hang: {
        type: String,
    },
    gia: {
        type: Number
    },
    image: {
         type: Array 
    },
}, {
    timestamps: true
})
module.exports = mongoose.model('car', Cars)