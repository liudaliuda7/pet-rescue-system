INSERT INTO rescue_station(id, name, address, phone, contact, description) VALUES
(1, '城南救助站', '上海市徐汇区漕宝路123号', '021-12345678', '李站长', '专注于救助流浪猫狗的非营利组织'),
(2, '北郊动物之家', '上海市宝山区友谊路456号', '021-87654321', '王主任', '配备专业兽医的综合救助站');

INSERT INTO user(id, username, password, name, role, phone, email, station_id) VALUES
(1, 'admin', '123456', '系统管理员', 'admin', '13800000000', 'admin@rescue.com', NULL),
(2, 'station1', '123456', '城南救助站管理员', 'station', '13800000001', 's1@rescue.com', 1),
(3, 'station2', '123456', '北郊动物之家管理员', 'station', '13800000002', 's2@rescue.com', 2),
(4, 'user1', '123456', '张三', 'user', '13900000001', 'zs@example.com', NULL),
(5, 'user2', '123456', '李四', 'user', '13900000002', 'ls@example.com', NULL);

INSERT INTO animal_type(id, name, description) VALUES
(1, '狗', '犬科动物'),
(2, '猫', '猫科动物'),
(3, '兔子', '小型哺乳动物');

INSERT INTO animal(id, name, type_id, gender, age, color, description, image, status, station_id, health_status) VALUES
(1, '小白', 1, '公', '2岁', '白色', '活泼好动的金毛犬', 'https://images.unsplash.com/photo-1552053831-71594a27632d?w=400', 'available', 1, '健康'),
(2, '黑妞', 2, '母', '1岁', '黑色', '温顺亲人的小黑猫', 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=400', 'available', 1, '健康'),
(3, '花花', 2, '母', '3岁', '三花', '已绝育，性格沉稳', 'https://images.unsplash.com/photo-1573865526739-10659fec78a5?w=400', 'available', 2, '健康'),
(4, '大黄', 1, '公', '4岁', '黄色', '聪明的中华田园犬', 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=400', 'adopted', 1, '健康'),
(5, '雪球', 3, '母', '6个月', '白色', '可爱的荷兰兔', 'https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?w=400', 'available', 2, '健康');

INSERT INTO notice(id, title, content) VALUES
(1, '关于流浪动物救助站招募志愿者的公告', '本站长期招募爱心志愿者，参与日常喂养、清洁、领养辅导等工作，有意者请联系站点负责人。'),
(2, '春季疫苗接种活动通知', '本月将开展春季流浪猫狗免费疫苗接种活动，欢迎大家积极参与。'),
(3, '领养前请仔细阅读领养须知', '为了保障被领养动物的福利，请领养人确保有稳定居所、合理收入及陪伴时间。');

INSERT INTO help_request(id, user_id, title, content, location, contact, status, station_id) VALUES
(1, 4, '小区里有只受伤的小猫', '在我们小区车库里发现一只受伤的小猫，腿部有伤无法行走，请求救助。', '上海市徐汇区漕宝路某小区', '13900000001', 'processing', 1),
(2, 5, '路边发现流浪狗', '路边一只流浪狗看起来很饿，希望有人能帮忙', '上海市宝山区友谊路', '13900000002', 'pending', NULL);

INSERT INTO help_record(id, request_id, station_id, content, handler, result) VALUES
(1, 1, 1, '已派遣志愿者前往现场，将小猫带回站点接受治疗', '李站长', '处理中');

INSERT INTO adoption(id, animal_id, user_id, status, reason, contact, address) VALUES
(1, 4, 4, 'approved', '家里有院子，时间充裕，希望领养一只田园犬作伴', '13900000001', '上海市徐汇区某小区');

INSERT INTO visit_record(id, adoption_id, content, status, visitor) VALUES
(1, 1, '回访发现大黄状态良好，主人照顾得很周到', '良好', '李站长');

INSERT INTO health_record(id, animal_id, content, doctor, record_date) VALUES
(1, 1, '常规体检，状态良好', '王医生', '2026-04-10'),
(2, 2, '已完成绝育手术，恢复良好', '王医生', '2026-04-15'),
(3, 1, '接种狂犬疫苗，观察30分钟无异常', '李医生', '2026-03-20'),
(4, 1, '驱虫处理，体外寄生虫已清除', '王医生', '2026-02-15'),
(5, 2, '口腔检查，牙齿健康', '张医生', '2026-03-05'),
(6, 3, '常规体检，已绝育，状态良好', '李医生', '2026-04-20'),
(7, 5, '首次体检，健康状况良好', '张医生', '2026-05-01');
