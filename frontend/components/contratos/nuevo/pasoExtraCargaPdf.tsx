"use client";

import { FileText, FileUp } from "lucide-react";
import { Input } from "@/components/ui/input";
import { useRef } from "react";

interface PasoCargaPdfProps {
	pdfFile: File | null;
	setPdfFile: (file: File | null) => void;
}

export default function PasoCargaPdf({ pdfFile, setPdfFile }: PasoCargaPdfProps) {
	const inputRef = useRef<HTMLInputElement | null>(null);

	const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		const f = e.target.files?.[0] || null;
		if (f && f.type !== "application/pdf") {
			// limpiar si no es PDF
			setPdfFile(null);
			if (inputRef.current) inputRef.current.value = "";
			return;
		}
		setPdfFile(f);
	};

	return (
		<>
			<div className="flex items-center gap-2 mb-4">
				<FileText className="h-5 w-5" />
				<span className="font-semibold">Archivo del contrato (PDF)</span>
			</div>
			<p>Seleccioná el PDF del contrato firmado. Este archivo se enviará al confirmar el registro.</p>

			<div className="mt-4 grid gap-3 bg-muted/40 rounded-xl border border-border p-4">
				<div className="flex items-center gap-3">
					<Input
						ref={inputRef}
						type="file"
						accept="application/pdf"
						onChange={handleFileChange}
					/>
				</div>
				{pdfFile && (
					<div className="text-sm text-muted-foreground">
						<span className="inline-flex items-center gap-2"><FileUp className="h-4 w-4"/> Archivo seleccionado:</span>
						<span className="ml-2 font-medium text-foreground break-all">{pdfFile.name}</span>
					</div>
				)}
				{!pdfFile && (
					<p className="text-sm text-muted-foreground">No seleccionaste ningún archivo.</p>
				)}
			</div>
		</>
	);
}

