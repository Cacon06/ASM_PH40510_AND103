var nodemailer = require("nodemailer");
const transporter = nodemailer.createTransport({
    service: "gmail",
    auth: {
        user: "huongvxph40510@fpt.edu.vn",
        pass: "huongvxph40510"
    }
});
module.exports = transporter 