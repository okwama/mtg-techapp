<?php
namespace Core;

use Dompdf\Dompdf;
use Dompdf\Options;

class PdfGenerator {
    public static function generate($html, $filename = 'report.pdf', $stream = true) {
        $options = new Options();
        $options->set('isHtml5ParserEnabled', true);
        $options->set('isRemoteEnabled', true);
        $options->set('defaultFont', 'Arial');

        $dompdf = new Dompdf($options);
        $dompdf->setPaper('A4', 'portrait');
        $dompdf->loadHtml($html);
        $dompdf->render();

        if ($stream) {
            $dompdf->stream($filename, ["Attachment" => false]);
        } else {
            return $dompdf->output();
        }
    }

    public static function renderReportTemplate($inspection) {
        // Simple HTML template for the inspection report
        $photosHtml = "";
        if (!empty($inspection['photos'])) {
            foreach ($inspection['photos'] as $photo) {
                $photosHtml .= "<div class='photo-item'>";
                $photosHtml .= "<img src='{$photo['photo_url']}' style='max-width: 100%;'>";
                $photosHtml .= "<p>{$photo['photo_type']}: {$photo['caption']}</p>";
                $photosHtml .= "</div>";
            }
        }

        return "
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; color: #333; }
                .header { text-align: center; border-bottom: 2px solid #336699; padding-bottom: 10px; }
                .section { margin-top: 20px; }
                .section-title { font-weight: bold; background: #f2f2f2; padding: 5px; border-left: 5px solid #336699; }
                .data-row { margin-bottom: 5px; }
                .label { font-weight: bold; width: 150px; display: inline-block; }
                .photo-item { margin-bottom: 20px; text-align: center; page-break-inside: avoid; }
            </style>
        </head>
        <body>
            <div class='header'>
                <h1>Vehicle Inspection Report</h1>
                <p>Report #: {$inspection['inspection_number']}</p>
            </div>
            
            <div class='section'>
                <div class='section-title'>Vehicle & Technician Information</div>
                <div class='data-row'><span class='label'>Vehicle:</span> {$inspection['registration_number']}</div>
                <div class='data-row'><span class='label'>Technician:</span> {$inspection['technician_name']}</div>
                <div class='data-row'><span class='label'>Date:</span> {$inspection['inspection_date']}</div>
                <div class='data-row'><span class='label'>Status:</span> " . strtoupper($inspection['status']) . "</div>
            </div>

            <div class='section'>
                <div class='section-title'>Overall Condition</div>
                <p>" . strtoupper($inspection['overall_condition'] ?? 'N/A') . "</p>
            </div>

            <div class='section'>
                <div class='section-title'>Summary & Findings</div>
                <p>" . nl2br($inspection['summary'] ?? 'No summary provided.') . "</p>
            </div>

            <div class='section'>
                <div class='section-title'>Inspection Photos</div>
                {$photosHtml}
            </div>
        </body>
        </html>
        ";
    }
}
