<?php
use Core\AuthMiddleware;

// Root Route
$router->add('GET', '/', 'HomeController@index');

// Auth Routes
$router->add('POST', '/auth/login', 'AuthController@login');
$router->add('GET', '/auth/profile', 'AuthController@getProfile', AuthMiddleware::class);

// Ticket Routes
$router->add('GET', '/tickets', 'TicketController@index', AuthMiddleware::class);
$router->add('GET', '/tickets/{id}', 'TicketController@show', AuthMiddleware::class);
$router->add('POST', '/tickets', 'TicketController@store', [AuthMiddleware::class, ['ADMIN', 'SUPERIOR']]);
$router->add('PUT', '/tickets/{id}', 'TicketController@update', [AuthMiddleware::class, ['ADMIN', 'SUPERIOR']]);

// Inspection Routes
$router->add('GET', '/inspections', 'InspectionController@index', AuthMiddleware::class);
$router->add('GET', '/inspections/scheduled', 'InspectionController@scheduled', AuthMiddleware::class);
$router->add('GET', '/inspections/clients', 'InspectionController@clients', AuthMiddleware::class);
$router->add('GET', '/inspections/clients/{clientId}/vehicles', 'InspectionController@vehicles', AuthMiddleware::class);
$router->add('POST', '/inspections', 'InspectionController@store', AuthMiddleware::class);
$router->add('GET', '/inspections/{id}', 'InspectionController@show', AuthMiddleware::class);
$router->add('PUT', '/inspections/{id}', 'InspectionController@update', AuthMiddleware::class);
$router->add('POST', '/inspections/{id}/submit', 'InspectionController@submit', AuthMiddleware::class);
$router->add('POST', '/inspections/{id}/approve', 'InspectionController@approve', [AuthMiddleware::class, ['ADMIN', 'SUPERIOR']]);
$router->add('POST', '/inspections/{id}/reject', 'InspectionController@reject', [AuthMiddleware::class, ['ADMIN', 'SUPERIOR']]);
$router->add('POST', '/inspections/{id}/photos', 'InspectionController@uploadPhotos', AuthMiddleware::class);
$router->add('GET', '/inspections/{id}/report', 'InspectionController@generateReport', AuthMiddleware::class);

// Service Approval Routes
$router->add('GET', '/service-approvals', 'ServiceController@index', AuthMiddleware::class);
$router->add('POST', '/service-approvals', 'ServiceController@store', AuthMiddleware::class);
$router->add('POST', '/service-approvals/{id}/approve', 'ServiceController@approve', [AuthMiddleware::class, ['ADMIN', 'SUPERIOR']]);
$router->add('POST', '/service-approvals/{id}/decline', 'ServiceController@decline', [AuthMiddleware::class, ['ADMIN', 'SUPERIOR']]);

// Parts Routes
$router->add('GET', '/parts', 'PartController@index', AuthMiddleware::class);
$router->add('POST', '/parts-requests', 'PartController@requestParts', AuthMiddleware::class);
$router->add('POST', '/parts-requests/{id}/approve', 'PartController@approveRequest', [AuthMiddleware::class, ['ADMIN', 'SUPERIOR']]);

// Log Routes
$router->add('GET', '/logs', 'LogController@index', [AuthMiddleware::class, ['ADMIN', 'SUPERIOR']]);
$router->add('GET', '/logs/{type}/{id}', 'LogController@getByResource', [AuthMiddleware::class, ['ADMIN', 'SUPERIOR']]);

// Shift & Station Routes
$router->add('GET', '/stations', 'ShiftController@index', AuthMiddleware::class);
$router->add('GET', '/shift/status', 'ShiftController@status', AuthMiddleware::class);
$router->add('POST', '/checkin', 'ShiftController@checkin', AuthMiddleware::class);
$router->add('POST', '/checkout', 'ShiftController@checkout', AuthMiddleware::class);
