BUSINESS REQUIREMENTS DOCUMENT
Technician Mobile Application
Vehicle Inspection, Diagnostics & Service Management System
 
1. EXECUTIVE SUMMARY
This Business Requirements Document (BRD) outlines the requirements for a mobile application designed for technicians to manage vehicle inspections, diagnostics, service requests, and parts inventory management. This is a technician-only application; all administrative functions (approvals, assignments, etc.) are handled through a separate web-based admin portal.

1.1 Business Objectives
•	Digitize vehicle inspection and diagnostic processes
•	Reduce inspection completion time by 40%
•	Enable real-time parts inventory tracking
•	Streamline service request submission
•	Improve technician productivity and accountability
 
2. USER ROLE
2.1 Technician Role (can also be attendant)
Field technicians responsible for performing vehicle inspections, diagnostics, and service work.

Key Responsibilities:
•	Check in/out at assigned stations
•	View assigned inspections
•	Perform vehicle inspections using digital checklists
•	Capture and upload inspection photos
•	Submit completed inspections for review
•	Create service request submissions
•	Request parts from inventory
•	View parts inventory at station
•	Update profile information
 
3. FUNCTIONAL REQUIREMENTS

3.1 Authentication & Security
FR-AUTH-001: User Login
•	Users authenticate using phone number and password
•	Support biometric authentication after initial login
•	Session timeout after 30 minutes of inactivity
•	Secure token-based authentication (JWT)

FR-AUTH-002: Profile Management
•	View personal profile information
•	Update contact details
•	Change password
•	View assigned station information

3.2 Check-in/Check-out System
FR-CHECKIN-001: Station Check-in
•	Manual station selection from dropdown
•	GPS location capture and validation
•	Display captured location address
•	Record check-in time automatically
•	Prevent operations if not checked in

FR-CHECKIN-002: Station Check-out
•	GPS location capture at check-out
•	Display captured location address
•	Record check-out time automatically
•	View shift duration
 
3.3 Inspection Management
FR-INSP-001: View Assigned Inspections
•	Display list of inspections assigned to logged-in technician
•	Calendar view showing scheduled inspections
•	Filter by status: pending, in-progress, completed
•	Search by client name, vehicle registration
•	Pull-to-refresh functionality
•	Show inspection priority and due date

FR-INSP-002: Perform Inspection
•	Load predefined inspection checklist
•	For each item: Pass / Fail / Needs Attention
•	Add notes/comments for each checklist item
•	Save progress (draft mode)
•	Resume incomplete inspections
•	Track inspection progress percentage

FR-INSP-003: Photo Upload
•	Capture photos using device camera
•	Categorize photos: Exterior, Interior, Engine, Damage, Odometer
•	Maximum 50 images per inspection
•	Add captions to photos
•	Compress images before upload
•	Queue photos for upload if offline
•	Delete/retake photos before submission

FR-INSP-004: Damage Reporting
•	Document vehicle damage with photos
•	Categorize damage severity
•	Add detailed damage descriptions
•	Link damage to specific vehicle areas
•	Multiple damage reports per inspection

FR-INSP-005: Submit Inspection
•	Validate all required fields completed
•	Review summary before submission
•	Submit for admin review
•	Display confirmation message
•	View submission status
 
3.4 Service Request Submission
FR-SERV-001: Create Service Request
•	Link service request to completed inspection
•	Service description (required, min 100 characters)
•	Select required parts from inventory
•	Specify labor hours estimate
•	Calculate estimated cost automatically
•	Submit for admin approval
•	View request status (pending/approved/declined)

FR-SERV-002: View Service Request Status
•	View all submitted service requests
•	Filter by status
•	View admin comments/feedback
•	Receive notifications on status changes
 
3.5 Parts Management
FR-PARTS-001: View Parts Inventory
•	Display parts available at assigned station
•	Search by part number, name, or category
•	Display: part name, number, quantity, price
•	Low stock indicator
•	View part details and specifications

FR-PARTS-002: Request Parts
•	Create parts requisition
•	Search and select parts from catalog
•	Specify quantity needed
•	Link to specific inspection or service request
•	Add reason/justification
•	Submit request for admin approval
•	View request status

FR-PARTS-003: View Parts Request History
•	View all submitted parts requests
•	Filter by status (pending/approved/declined/fulfilled)
•	View admin comments
•	Receive notifications on status changes
 
3.7 OBD Reader & Diagnostics
FR-OBD-001: Connect to Vehicle
•	Connect to vehicle via Bluetooth OBD-II adapter
•	Support ELM327-compatible adapters
•	Display connection status
•	Auto-reconnect on connection loss
•	Manual disconnect option

FR-OBD-002: Read Diagnostic Trouble Codes (DTCs)
•	Scan for active diagnostic trouble codes
•	Display DTC code and description
•	Show freeze frame data when available
•	Categorize codes by system (Engine, Transmission, ABS, etc.)
•	Save DTC history for reference

FR-OBD-003: Clear Diagnostic Codes
•	Clear diagnostic trouble codes after repairs
•	Confirm action before clearing
•	Log clearing action with timestamp
•	Require technician confirmation

FR-OBD-004: Live Data Monitoring
•	Display real-time vehicle parameters
•	Show: RPM, Speed, Coolant Temp, Engine Load, Fuel Pressure
•	Customizable data display
•	Record data snapshots
•	Export data for analysis

FR-OBD-005: Link to Inspection
•	Attach OBD scan results to inspection report
•	Include DTCs in inspection documentation
•	Add OBD data to service requests
•	Store scan history per vehicle
 
3.8 Offline Functionality
FR-OFFL-001: Offline Operations
•	Cache assigned inspections locally
•	Complete inspections offline
•	Capture photos offline
•	Save inspection drafts locally
•	Queue actions for sync when online

FR-OFFL-002: Data Synchronization
•	Auto-sync when connection restored
•	Display sync status indicator
•	Retry failed syncs
•	Show pending uploads count
•	Manual sync trigger option
 
4. TECHNICAL REQUIREMENTS

4.1 Current Implementation
Mobile Application
•	Platform: Android (Kotlin)
•	UI Framework: Jetpack Compose
•	Architecture: MVVM (Model-View-ViewModel)
•	Navigation: Compose Navigation
•	HTTP Client: Retrofit + OkHttp
•	Serialization: Kotlinx Serialization
•	Dependency Injection: Manual Factory Pattern
•	Image Loading: Coil
•	Camera: CameraX
•	GPS: Google Play Services Location
•	OBD-II: Android-OBD-Reader or custom Bluetooth implementation
•	Local Storage: DataStore (for preferences)

Backend API
•	Server: PHP (existing)
•	Database: MySQL/MariaDB (existing)
•	Authentication: JWT (JSON Web Tokens)
•	Image Storage: Server file system or cloud storage

4.2 Database Tables
The following tables are used by the technician app (managed by backend):

Table: staff
•	User authentication and profile data

Table: stations
•	Station information for check-in/check-out

Table: shifts
•	Check-in/check-out records with GPS coordinates

Table: conversions
•	Vehicle conversion/inspection assignments

Table: inspections
•	Inspection records and status

Table: inspection_photos
•	Photos linked to inspections

Table: service_approvals
•	Service request submissions

Table: parts_requests
•	Parts requisition requests

Table: Parts
•	Parts inventory catalog
 
4.3 API Endpoints (Technician App)
Authentication
•	POST /api/auth/login - User login
•	POST /api/auth/logout - User logout
•	GET /api/auth/profile - Get user profile
•	PUT /api/auth/profile - Update profile

Check-in/Check-out
•	GET /api/stations - Get available stations
•	GET /api/shift/status - Get current shift status
•	POST /api/checkin - Check in at station
•	POST /api/checkout - Check out from station
•	GET /api/shift/history - Get check-in history

Inspections
•	GET /api/inspections - Get assigned inspections
•	GET /api/inspections/:id - Get inspection details
•	POST /api/inspections - Create new inspection
•	PUT /api/inspections/:id - Update inspection
•	POST /api/inspections/:id/submit - Submit inspection for review
•	POST /api/inspections/:id/photos - Upload inspection photos
•	DELETE /api/inspections/:id/photos/:photoId - Delete photo

Service Requests
•	GET /api/service-requests - Get technician's service requests
•	POST /api/service-requests - Create service request
•	GET /api/service-requests/:id - Get request details

Parts Management
•	GET /api/parts - Get parts inventory at station
•	GET /api/parts/:id - Get part details
•	GET /api/parts-requests - Get technician's parts requests
•	POST /api/parts-requests - Create parts request
•	GET /api/parts-requests/:id - Get request details
 
4.4 Performance Requirements
•	App launch time: < 3 seconds
•	API response time: < 2 seconds for standard requests
•	Image upload: support up to 5MB per image
•	Smooth 60fps UI animations
•	Offline mode fully functional

4.5 Security Requirements
•	SSL/TLS encryption for all API communications
•	JWT token expiration: 24 hours
•	Secure local storage for cached data
•	Input validation and sanitization
•	Location permissions properly requested
•	Camera permissions properly requested
 
5. IMPLEMENTATION STATUS

5.1 Phase 1: Core Functionality ✅ COMPLETE
•	✅ Authentication & authorization
•	✅ Check-in/Check-out system with GPS and location address
•	✅ Basic inspection module (dashboard, setup, checklist, damage report)
•	✅ Profile management
•	⏳ Parts inventory view - IN PROGRESS
•	⏳ Service request submission - PENDING
•	⏳ Parts request workflow - PENDING

5.2 Phase 2: Advanced Features - PENDING
•	Complete inspection workflow with photo management
•	Service request tracking and status updates
•	Parts request tracking and status updates
•	Offline functionality with local caching
•	Push notifications for status updates

5.3 Phase 3: Polish & Optimization - PENDING
•	Performance optimization
•	Enhanced offline capabilities
•	User experience improvements
•	Comprehensive testing
•	Documentation
 
6. TESTING & ACCEPTANCE CRITERIA

6.1 Testing Requirements
•	Unit Testing: Core business logic
•	Integration Testing: API communication
•	UI Testing: Critical user flows
•	Performance Testing: Meet performance targets
•	Offline Testing: All offline scenarios
•	User Acceptance Testing: Technician sign-off

6.2 Acceptance Criteria
•	All functional requirements implemented and working
•	All user workflows can be completed successfully
•	Database integration complete and functional
•	All API endpoints functional
•	Performance requirements met
•	Security requirements met
•	Zero critical bugs
•	< 5 high-priority bugs
•	Successful UAT completion
•	User satisfaction rating > 4.0/5.0
 
7. ASSUMPTIONS & CONSTRAINTS

7.1 Assumptions
•	Existing backend API will be available throughout development
•	Technicians have Android smartphones (Android 8+)
•	Internet connectivity available at stations (with offline fallback)
•	Server storage available for photos
•	Admin approvals handled through separate web portal

7.2 Constraints
•	Android platform only (no iOS version)
•	Must integrate with existing PHP backend and MySQL database
•	No modifications to existing database tables
•	Admin features not included in mobile app
 
8. NEXT STEPS

8.1 Immediate Priorities
1. Parts Inventory View - Display parts at assigned station with search
2. Service Request Submission - Create and submit service requests
3. Parts Request Workflow - Request parts with approval tracking
4. OBD Reader Integration - Connect to vehicles and read diagnostic codes
5. Photo Management - Enhanced photo upload and management
6. Offline Functionality - Complete offline mode implementation

8.2 Future Enhancements
•	Push notifications for real-time updates
•	Enhanced reporting and history views
•	Performance analytics and metrics
•	In-app tutorials and help system

--- END OF DOCUMENT ---
