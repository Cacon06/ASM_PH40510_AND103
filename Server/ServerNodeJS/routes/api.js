var express = require('express');
var router = express.Router();

const Cars = require('../models/cars')
const Users = require('../models/users')
const Upload = require('../config/common/upload');

router.get('/get-all-car', async (req, res) => {
    try {
        const data = await Cars.find().populate();
        res.json({
            "status": 200,
            "messenger": "Danh sách car",
            "data": data
        })
    } catch (error) {
        console.log(error);
    }
})

router.post('/add-car', async (req, res) => {
    try {
        const data = req.body;
        const newCar = new Cars({
            ten: data.ten,
            namSX: data.namSX,
            hang: data.hang,
            gia: data.gia,
            image: data.image,
        });
        const result = await newCar.save();
        if (result) {
            res.json({
                "status": 200,
                "messenger": "Thêm thành công",
                "data": result
            })
        } else {
            res.json({
                "status": 400,
                "messenger": "Thêm không thành công",
                "data": []
            })

        }
    } catch (error) {
        console.log(error);
    }
})

router.get('/get-list-car', async (req, res, next) => {
    const authHeader = req.headers['authorization'];
    console.log(authHeader);
    if (!authHeader) {
        return res.sendStatus(401); // Kiểm tra xem header Authorization có tồn tại không
    }

    const token = authHeader.split(' ')[1];

    if (!token) {
        return res.sendStatus(401); // Kiểm tra xem token có tồn tại không
    }

    try {
        const payload = JWT.verify(token, SECRETKEY); // Xác thực token
        console.log(payload);

        const data = await Cars.find().populate('id_distributor');
        res.json({
            "status": 200,
            "message": 'Danh sách car',
            "data": data
        });
    } catch (error) {
        if (error instanceof JWT.TokenExpiredError) {
            return res.sendStatus(401); // Token hết hạn
        } else {
            console.error(error);
            return res.sendStatus(403); // Lỗi xác thực
        }
    }
});

router.put('/update-car-by-id/:id', Upload.array('image', 5), async (req, res) => {
    try {
        const { id } = req.params
        const data = req.body;
        const { files } = req;

        let url1;
        const updateCar = await Cars.findById(id)
        if (files && files.length > 0) {
            url1 = files.map((file) => `${req.protocol}://${req.get("host")}/uploads/${file.filename}`);

        }
        if (url1 == null) {
            url1 = updateCar.image;
        }

        let result = null;
        if (updateCar) {
            updateCar.ten = data.ten ?? updatefruit.ten,
            updateCar.namSX = data.namSX ?? updatefruit.namSX,
            updateCar.hang = data.hang ?? updatefruit.hang,
            updateCar.gia = data.gia ?? updatefruit.gia,


            updateCar.image = url1,
            result = (await updatefruit.save());
        }
        if (result) {
            res.json({
                'status': 200,
                'messenger': 'Cập nhật thành công',
                'data': result
            })
        } else {
            res.json({
                'status': 400,
                'messenger': 'Cập nhật không thành công',
                'data': []
            })
        }
    } catch (error) {
        console.log(error);
    }
})

router.delete('/destroy-car-by-id/:id', async (req, res) => {
    try {
        const { id } = req.params
        const result = (await Cars.findByIdAndDelete(id));
        if (result) {
            res.json({
                "status": 200,
                "messenger": "Xóa thành công",
                "data": result
            })
        } else {
            res.json({
                "status": 400,
                "messenger": "Lỗi! xóa không thành công",
                "data": []
            })
        }
    } catch (error) {
        console.log(error);
    }
})

//upload image
router.post('/add-car-with-file-image', Upload.array('image', 5), async (req, res) => {
    //Upload.array('image',5) => up nhiều file tối đa là 5
    //upload.single('image') => up load 1 file
    try {
        const data = req.body; // Lấy dữ liệu từ body
        const { files } = req //files nếu upload nhiều, file nếu upload 1 file
        const urlsImage =
            files.map((file) => `${req.protocol}://${req.get("host")}/uploads/${file.filename}`)
        //url hình ảnh sẽ được lưu dưới dạng: http://localhost:3000/upload/filename
        const newCar = new Cars({
            ten: data.ten,
            namSX: data.namSX,
            hang: data.hang,
            gia: data.gia,
            image: data.image,
        });
        const result = (await newCar.save()); //Thêm vào database
        if (result) {// Nếu thêm thành công result !null trả về dữ liệu
            res.json({
                "status": 200,
                "messenger": "Thêm thành công",
                "data": result
            })
        } else {// Nếu thêm không thành công result null, thông báo không thành công
            res.json({
                "status": 400,
                "messenger": "Lỗi, thêm không thành công",
                "data": []
            })
        }
    } catch (error) {
        console.log(error);
    }
});

router.post('/add-user', Upload.single('avartar'), async (req, res) => {
    try {
        const data = req.body;
        const file = req.file;
        const imageUrl = `${req.protocol}://${req.get("host")}/uploads/${file.filename}`;

        const newUser = new Users({
            username: data.username,
            password: data.password,
            email: data.email,
            name: data.name,
            avartar: imageUrl,
        });

        const result = await newUser.save();
        if (result) {
            res.json({
                "status": 200,
                "message": "Thêm thành công",
                "data": result
            });
        } else {
            res.json({
                "status": 400,
                "message": "Thêm không thành công",
                "data": []
            });
        }
    } catch (error) {
        console.log(error);
        res.status(500).json({
            "status": 500,
            "message": "Đã xảy ra lỗi",
            "data": []
        });
    }
});

router.get('/getAllUser', async (req, res) => {
    try {
        const users = await Users.find();
        res.status(200).json({
            status: 200,
            message: 'Danh sách người dùng',
            data: users
        });
    } catch (error) {
        console.log(error);
    }

})

const Transporter = require('../config/common/mail')
router.post('/register-send-email', Upload.single('avartar'), async (req, res) => {
    try {
        const data = req.body;
        const { file } = req
        const newUser = Users({
            username: data.username,
            password: data.password,
            email: data.email,
            name: data.name,
            avartar: `${req.protocol}://${req.get("host")}/uploads/${file.filename}`,
            //url avatar http://localhost:3000/uploads/filename
        })
        const result = await newUser.save()
        if (result) { //Gửi mail
            const mailOptions = {
                from: "thanghtph31577@fpt.edu.vn", //email gửi đi
                to: result.email, // email nhận
                subject: "Đăng ký thành công", //subject
                text: "Cảm ơn bạn đã đăng ký", // nội dung mail
            };
            // Nếu thêm thành công result !null trả về dữ liệu
            await Transporter.sendMail(mailOptions); // gửi mail
            res.json({
                "status": 200,
                "messenger": "Thêm thành công",
                "data": result
            })
        } else {// Nếu thêm không thành công result null, thông báo không thành công
            res.json({
                "status": 400,
                "messenger": "Lỗi, thêm không thành công",
                "data": []
            })
        }
    } catch (error) {
        console.log(error);
    }
})


const JWT = require('jsonwebtoken');
const SECRETKEY = "FPTPOLYTECHNIC"
router.post('/login', async (req, res) => {
    try {
        const { username, password } = req.body;
        const user = await Users.findOne({ username, password })
        if (user) {
            //Token người dùng sẽ sử dụng gửi lên trên header mỗi lần muốn gọi api
            const token = JWT.sign({ id: user._id }, SECRETKEY, { expiresIn: '1h' });
            //Khi token hết hạn, người dùng sẽ call 1 api khác để lấy token mới
            //Lúc này người dùng sẽ truyền refreshToken lên để nhận về 1 cặp token,refreshToken mới
            //Nếu cả 2 token đều hết hạn người dùng sẽ phải thoát app và đăng nhập lại
            const refreshToken = JWT.sign({ id: user._id }, SECRETKEY, { expiresIn: '1d' })
            //expiresIn thời gian token
            res.json({
                "status": 200,
                "messenger": "Đăng nhâp thành công",
                "data": user,
                "token": token,
                "refreshToken": refreshToken
            })
        } else {
            // Nếu thêm không thành công result null, thông báo không thành công
            res.json({
                "status": 400,
                "messenger": "Lỗi, đăng nhập không thành công",
                "data": []
            })
        }
    } catch (error) {
        console.log(error);
    }
})

module.exports = router;