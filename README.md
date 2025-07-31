# Noon Careers Portal

An unofficial careers website for Noon (Middle East E-Commerce platform) with integrated Applicant Tracking System (ATS) features. This educational project demonstrates a full-stack career portal with advanced features including coding assessments, referral systems, and comprehensive candidate management.

## 🌟 Features

### Core Career Portal Features
- **Job Listings Management**: Browse, search, and filter job vacancies by type, experience level, and location
- **User Authentication**: Secure JWT-based authentication with role-based access control
- **Profile Management**: Comprehensive user profiles with education and experience tracking
- **Job Applications**: End-to-end application process with status tracking
- **Resume Upload**: Cloud-based resume storage using Cloudinary integration

### ATS (Applicant Tracking System) Features
- **Coding Assessments**: Interactive coding challenges with real-time code execution
- **Assessment Management**: Create and manage programming questions with test cases
- **Candidate Scoring**: Automated scoring system for coding assessments
- **Application Tracking**: Monitor candidate progress through different application stages

### Advanced Features
- **Employee Referral System**: Internal referral management with status tracking
- **Email Notifications**: Automated email system with templated messages
- **Code Execution Engine**: Integration with Judge0 API for running and evaluating code
- **Role-Based Access**: Multiple user roles (Admin, Recruiter, Employee, Candidate)
- **Admin Dashboard**: User management and system administration tools

## 🏗️ Architecture

### Technology Stack
- **Backend**: Spring Boot 3.5.3 with Java 24
- **Database**: MySQL with JPA/Hibernate
- **Security**: Spring Security with JWT authentication
- **Code Execution**: Judge0 API integration
- **Email**: Spring Mail with Gmail SMTP
- **File Storage**: Cloudinary for resume uploads
- **Containerization**: Docker with Docker Compose

### System Components
- **Main Application**: Spring Boot REST API
- **Database**: MySQL for data persistence
- **Code Judge**: Judge0 service for code execution and testing
- **Redis**: Caching for Judge0 service
- **Email Service**: SMTP integration for notifications

## 🚀 Getting Started

### Prerequisites
- Java 24 or higher
- Maven 3.6+
- Docker and Docker Compose
- MySQL (if running locally)

### Environment Variables
Create a `.env` file with the following variables:

```env
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/career_db?createDatabaseIfNotExist=true
SPRING_DATASOURCE_USERNAME=noon_user
SPRING_DATASOURCE_PASSWORD=noon_password

# JWT Configuration
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION_SECONDS=3600

# Email Configuration
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password

# Admin Account
ADMIN_EMAIL=admin@noon.careers
ADMIN_PASSWORD=admin-password

# Cloudinary Configuration
CLOUDINARY_URL=cloudinary://api_key:api_secret@cloud_name

# Judge0 API Configuration
JUDGE0_API_URL=http://localhost:2358
RAPID_API_KEY=your-rapid-api-key
RAPID_API_HOST=judge0-ce.p.rapidapi.com
```

### Installation & Setup

#### Option 1: Docker Compose (Recommended)
```bash
# Clone the repository
git clone <repository-url>
cd noon-careers-portal

# Create environment file
cp .env.example .env
# Edit .env with your configuration

# Build and run with Docker Compose
docker-compose up --build

# The application will be available at:
# - API: http://localhost:8080
# - Judge0: http://localhost:2358
```

#### Option 2: Local Development
```bash
# Start MySQL service
sudo systemctl start mysql

# Create database
mysql -u root -p -e "CREATE DATABASE career_db;"

# Build the application
mvn clean package

# Run the application
java -jar target/careers-portal.jar

# Or run with Maven
mvn spring-boot:run
```

## 📚 API Documentation

### Authentication Endpoints
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/verify-email` - Email verification
- `POST /api/auth/forgot-password` - Password reset request
- `POST /api/auth/reset-password` - Password reset

### Job Management
- `GET /api/jobs` - List all active jobs
- `GET /api/jobs/{id}` - Get job details
- `GET /api/jobs/search?keyword=` - Search jobs
- `GET /api/jobs/filter?jobType=&experienceLevel=` - Filter jobs
- `POST /api/jobs` - Create new job (Admin/Recruiter)
- `PUT /api/jobs/{id}` - Update job (Admin/Recruiter)

### Applications
- `POST /api/applications` - Submit job application
- `GET /api/applications/user/{userId}` - Get user applications
- `PUT /api/applications/{id}/status` - Update application status

### Assessments
- `GET /api/assessments/{token}` - Get assessment by token
- `POST /api/assessments/{token}/submit` - Submit assessment solution
- `POST /api/assessments/{token}/run` - Test code against sample cases

### User Profile
- `GET /api/profile/{userId}/education` - Get education history
- `POST /api/profile/{userId}/education` - Add education
- `GET /api/profile/{userId}/experience` - Get work experience
- `POST /api/profile/{userId}/experience` - Add experience

### Referrals
- `POST /api/referrals` - Create referral (Employee)
- `GET /api/referrals/sent` - Get sent referrals
- `GET /api/referrals/received` - Get received referrals
- `PUT /api/referrals/{id}/accept` - Accept referral

### Admin
- `GET /api/admin/users` - List all users
- `PUT /api/admin/users/{id}/role` - Update user role
- `POST /api/admin/questions` - Create assessment question

## 🔧 Configuration

### Application Properties
The application uses Spring Boot's configuration system. Key configurations:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### User Roles
- **ADMIN**: Full system access, user management
- **RECRUITER**: Job and application management
- **EMPLOYEE**: Can create referrals, limited access
- **USER/CANDIDATE**: Can apply for jobs, take assessments

## 🧪 Code Assessment System

The platform includes a sophisticated coding assessment system:

### Features
- **Multi-language Support**: Java, Python, C++, JavaScript, and more
- **Test Case Validation**: Automated testing against predefined test cases
- **Real-time Execution**: Code runs in isolated containers
- **Performance Metrics**: Execution time and memory usage tracking
- **Plagiarism Prevention**: Unique assessment tokens and time limits

### Assessment Flow
1. Candidate receives assessment invitation via email
2. Access assessment using unique token
3. Solve coding problem(s) in browser editor
4. Submit solution for automated testing
5. Results automatically scored and recorded

## 🔒 Security Features

- **JWT Authentication**: Stateless authentication with configurable expiration
- **Role-Based Access Control**: Method-level security annotations
- **Email Verification**: Account activation via email confirmation
- **Password Reset**: Secure password recovery flow
- **CORS Configuration**: Configurable cross-origin resource sharing
- **Input Validation**: Request validation and sanitization

## 📧 Email System

Comprehensive email notification system with:
- **Template Management**: Reusable email templates
- **Automated Notifications**: Application status updates, assessment invitations
- **Referral Notifications**: Referral status and acceptance emails
- **Admin Notifications**: System alerts and user management updates

## 🐳 Docker Support

The application is fully containerized with:
- **Multi-container Setup**: Application, database, and code execution services
- **Volume Persistence**: Data persistence across container restarts
- **Network Isolation**: Secure inter-service communication
- **Health Checks**: Service health monitoring
- **Scalable Architecture**: Easy horizontal scaling

## 📊 Database Schema

### Key Entities
- **Users**: User accounts with roles and authentication
- **JobVacancy**: Job postings with requirements and details
- **JobApplication**: Application submissions with status tracking
- **Assessment**: Coding assessments with questions and results
- **Referral**: Employee referral system
- **Question/TestCase**: Assessment questions and validation cases

## 🤝 Contributing

This is an educational project. To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is for educational purposes. Please note that this is an unofficial implementation and is not affiliated with Noon.com.

## 🚨 Disclaimer

This project is created for educational and learning purposes only. It is not officially affiliated with, endorsed by, or connected to Noon.com or any of its subsidiaries or affiliates. The Noon name and any related marks are trademarks of their respective owners.

## 🔮 Future Enhancements

- **Video Interviews**: Integration with video calling APIs
- **Advanced Analytics**: Recruitment metrics and dashboards
- **Mobile App**: React Native mobile application
- **AI Integration**: Resume parsing and candidate matching
- **Internationalization**: Multi-language support

## 📞 Support

For questions or support regarding this educational project, please create an issue in the repository.

---

**Built with ❤️ for learning and educational purposes**
