// src/components/Table.jsx
import React from "react";

const Table = ({ columns, data, onRowSelect, selectedRowIndex }) => {
  return (
    <div className="overflow-x-auto w-full">
      <table className="w-full text-xs sm:text-sm text-left">
        <thead style={{ backgroundColor: '#F5F9FF' }}>
          <tr>
            {columns.map((col) => (
              <th 
                key={col.accessor} 
                className="px-4 sm:px-8 py-4 sm:py-5 font-medium text-xs uppercase tracking-wide"
                style={{ color: '#0095FF' }}
              >
                {col.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.length > 0 ? (
            data.map((row, i) => (
              <tr
                key={i}
                onClick={() => onRowSelect && onRowSelect(i)}
                className={`hover:bg-blue-50 transition border-b border-gray-50 last:border-none touch-manipulation ${
                  selectedRowIndex === i ? 'bg-blue-100' : ''
                } ${onRowSelect ? 'cursor-pointer' : ''}`}
              >
                {columns.map((col) => (
                  <td 
                    key={col.accessor} 
                    className="px-4 sm:px-8 py-4 sm:py-5 text-xs sm:text-sm"
                    style={{ color: '#4A5568' }}
                  >
                    <div className="truncate">
                      {row[col.accessor]}
                    </div>
                  </td>
                ))}
              </tr>
            ))
          ) : (
            <tr>
              <td
                colSpan={columns.length}
                className="text-center py-6 sm:py-8 text-xs sm:text-sm"
                style={{ color: '#999999' }}
              >
                No hay datos disponibles
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default Table;
